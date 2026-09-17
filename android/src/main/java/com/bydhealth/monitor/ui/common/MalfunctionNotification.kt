package com.bydhealth.monitor.ui.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.bydhealth.monitor.domain.model.MalfunctionInfo
import com.bydhealth.monitor.domain.model.Severity

object MalfunctionNotification {

    const val CHANNEL_ID = "byd_malfunction"
    private const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "차량 고장 알림",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply { description = "BYD 차량 긴급 고장 코드 감지 시 알림" }

        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun notify(context: Context, malfunctions: List<MalfunctionInfo>) {
        val critical = malfunctions.filter { it.severity == Severity.CRITICAL }
        if (critical.isEmpty()) return

        val title = if (critical.size == 1) "⚠️ ${critical.first().name}"
                    else "⚠️ 긴급 고장 ${critical.size}건 감지"
        val body = critical.joinToString("\n") { "• ${it.action}" }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, notification)
    }
}
