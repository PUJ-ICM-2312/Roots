package com.example.roots.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.roots.MainActivity
import com.example.roots.R
import com.example.roots.CHAT_NOTIFICATION_CHANNEL_ID
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest

object NotificationUtils {

    private var lastNotificationId = 1000 // Para generar IDs únicos

    fun showNewMessageNotification(
        context: Context,
        senderName: String,
        messageText: String,
        chatId: String,
        receptorId: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_action", "open_chat")
            putExtra("chat_id", chatId)
            putExtra("receptor_id", receptorId)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            chatId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationId = chatId.hashCode()

        val builder = NotificationCompat.Builder(context, CHAT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(senderName)
            .setContentText(messageText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // ✅ Verifica que el permiso esté concedido
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } else {
            println("⚠️ Permiso POST_NOTIFICATIONS no concedido. No se mostró la notificación.")
        }
    }

}
