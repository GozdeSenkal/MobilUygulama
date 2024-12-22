package com.example.yeniproje

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Bildirim gönder
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default_channel"

        // Android 8.0 ve sonrası için bildirim kanalı oluşturma
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Bildirim detaylarını oluşturun
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Yemekteyiz")
            .setContentText("Yeni tariflere göz atmak ister misin ! tıkla ")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        // Bildirimi göster
        notificationManager.notify(1, notification)
    }
}
