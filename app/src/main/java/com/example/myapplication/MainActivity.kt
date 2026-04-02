package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.service.controls.templates.ControlButton
import android.content.Intent
import android.text.Layout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MyApplicationApp()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@PreviewScreenSizes
@Composable
fun MyApplicationApp() {
    val context = LocalContext.current



    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center


    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(250.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(

                onClick = {
                    val serviceIntent = Intent(context, ForegroundService::class.java)
                    serviceIntent.putExtra("intentExtra", "ForegroundService")
                    ContextCompat.startForegroundService(context, serviceIntent)
                },
                modifier = Modifier.height(120.dp).width(320.dp)
            ) {
                Text(
                    text = "Start Service"
                )
            }

            Button(

                onClick = {
                    val serviceIntent = Intent(context, ForegroundService::class.java)
                    serviceIntent.putExtra("intentExtra", "ForegroundService")
                    context.stopService(serviceIntent)
                },
                modifier = Modifier.height(120.dp).width(320.dp)
            ) {
                Text(
                    text = "Stop Service"
                )
            }
        }

    }
}



