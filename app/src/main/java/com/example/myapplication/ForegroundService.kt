package com.example.myapplication


import android.app.Notification
import android.app.Service
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.util.Log
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlin.jvm.java
import android.content.ContentValues.TAG
import android.content.pm.ServiceInfo
import java.io.IOException


class ForegroundService : Service() {

    private var server: LocalServer? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("intentExtra")
        createNotificationChannel()


        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification: Notification = NotificationCompat.Builder(this, "1")
            .setContentText(input ?: "Service is running")
            .setContentTitle("Foreground Service")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
            Log.d(TAG, "startForeground: Успешно запущен")

            startServer()

            return START_NOT_STICKY
        }



        return START_STICKY
}

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "1",
                "Foreground",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun startServer() {
        try {
            server = LocalServer(8085)
            server?.start()
            Log.i("HttpServer", "✅ Сервер запущен на порту 8080")
        } catch (e: IOException) {
            Log.e("HttpServer", "❌ Ошибка запуска сервера: ${e.message}")
        }
    }

        override fun onBind(p0: Intent?): IBinder? {
            return null // Не привязываем сервис
        }

        override fun onDestroy() {
            super.onDestroy()
            Log.d("NewService", "Сервис остановлен")
        }

}