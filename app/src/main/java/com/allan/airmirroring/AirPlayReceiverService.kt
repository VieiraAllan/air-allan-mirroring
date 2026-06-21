package com.allan.airmirroring

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder

class AirPlayReceiverService : Service() {

    companion object {
        const val ACTION_START = "com.allan.airmirroring.action.START"
        const val ACTION_STOP = "com.allan.airmirroring.action.STOP"
        private const val CHANNEL_ID = "airplay_receiver_channel"
        private const val NOTIFICATION_ID = 1001
    }

    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startReceiver()
            ACTION_STOP -> stopReceiver()
        }
        return START_STICKY
    }

    private fun startReceiver() {
        if (isRunning) return
        isRunning = true
        startForeground(NOTIFICATION_ID, buildNotification("Receptor AirPlay ativo"))
    }

    private fun stopReceiver() {
        isRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(text: String): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }

        return builder
            .setContentTitle("Air Állan Mirroring")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "AirPlay Receiver",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}