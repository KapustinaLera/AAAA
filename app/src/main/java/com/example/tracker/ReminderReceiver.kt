package com.example.tracker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.tracker.utils.NotificationHelper
import com.example.tracker.utils.PreferencesManager

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferencesManager(context)

        // Проверяем, включены ли уведомления
        if (prefs.areNotificationsEnabled()) {
            val name = prefs.getUserName()
            val days = ((System.currentTimeMillis() - prefs.getQuitDate()) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)

            // ПОЛУЧАЕМ ПРАВИЛЬНОЕ СКЛОНЕНИЕ (1 день, 2 дня, 5 дней)
            val daysString = context.resources.getQuantityString(R.plurals.days_count, days, days)

            // ИСПОЛЬЗУЕМ СТРОКОВЫЕ РЕСУРСЫ С ПРАВИЛЬНЫМ СКЛОНЕНИЕМ
            val text = if (name.isNotEmpty()) {
                context.getString(R.string.notification_text_with_name, name, daysString)
            } else {
                context.getString(R.string.notification_text_no_name, daysString)
            }

            // ВОТ ЭТОТ БЛОК ТЫ СЛУЧАЙНО УДАЛИЛА:
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(context, "tracker_reminder_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(1, notification)
        }

        // ВАЖНО: Перезапускаем будильник на следующий день
        // (так как setExactAndAllowWhileIdle работает только 1 раз)
        NotificationHelper.scheduleDailyReminder(context)
    }
}