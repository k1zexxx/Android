package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val btnStart = findViewById<Button>(R.id.button)
        val btnStop = findViewById<Button>(R.id.button2)

        btnStart.setOnClickListener {
            val serviceIntent = Intent(this, ForegroundService::class.java)
            serviceIntent.putExtra("intentExtra", "ForegroundService")
            ContextCompat.startForegroundService(this, serviceIntent)
        }

        btnStop.setOnClickListener {
            val serviceIntent = Intent(this, ForegroundService::class.java)
            serviceIntent.putExtra("intentExtra", "ForegroundService")
            stopService(serviceIntent)
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ServerControlScreen()
            }
        }
    }

    @Composable
    fun ServerControlScreen() {
        val context = LocalContext.current
        var isServiceRunning by remember { mutableStateOf(false) }
        var serverUrl by remember { mutableStateOf("") }
        var statusMessage by remember { mutableStateOf("Not started") }
        var isChecking by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            // Заголовок
            Text(
                text = "📱 Test Server",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Карточка статуса
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isServiceRunning)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isServiceRunning)
                            androidx.compose.material.icons.Icons.Default.CheckCircle
                        else
                            androidx.compose.material.icons.Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = if (isServiceRunning)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isServiceRunning) "Service Active" else "Service Inactive",
                        style = MaterialTheme.typography.titleLarge
                    )

                    if (isServiceRunning) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = serverUrl.ifEmpty { "Starting..." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (statusMessage.contains("✅"))
                            MaterialTheme.colorScheme.primary
                        else if (statusMessage.contains("❌"))
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isChecking) {
                        Spacer(modifier = Modifier.height(4.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопки управления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        try {
                            val serviceIntent = Intent(context, ForegroundService::class.java)
                            serviceIntent.putExtra("intentExtra", "Test Server Started")
                            ContextCompat.startForegroundService(context, serviceIntent)
                            isServiceRunning = true
                            statusMessage = "🔄 Starting server..."
                            Log.d("MainActivity", "Start button clicked")
                        } catch (e: Exception) {
                            statusMessage = "Error: ${e.message}"
                            Log.e("MainActivity", "Error starting service", e)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isServiceRunning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Server")
                }

                Button(
                    onClick = {
                        try {
                            val serviceIntent = Intent(context, ForegroundService::class.java)
                            context.stopService(serviceIntent)
                            isServiceRunning = false
                            statusMessage = "Server stopped"
                            serverUrl = ""
                            Log.d("MainActivity", "Stop button clicked")
                        } catch (e: Exception) {
                            statusMessage = "Error: ${e.message}"
                            Log.e("MainActivity", "Error stopping service", e)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isServiceRunning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Stop,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stop Server")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка проверки статуса
            OutlinedButton(
                onClick = { checkServerStatus() },
                modifier = Modifier.fillMaxWidth(),
                enabled = isServiceRunning
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Check Status")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Информационная карточка
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ℹ️ Server Information",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Port: 8085\n• API Endpoints:\n  - GET /info\n  - GET /api/messages\n  - POST /api/messages?text=...\n  - DELETE /api/messages/{id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

