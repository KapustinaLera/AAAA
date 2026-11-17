package com.example.tracker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.tracker.utils.NotificationHelper
import com.example.tracker.utils.PreferencesManager
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferencesManager(context)
        if (!prefs.areNotificationsEnabled()) return

        val name = prefs.getUserName()

        // Считаем дни от последнего срыва
        val lastRelapseDate = prefs.getLastRelapseDate()

        val latestRelapseDayCal = Calendar.getInstance().apply {
            timeInMillis = lastRelapseDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val days = ((todayCal.timeInMillis - latestRelapseDayCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)

        val daysString = context.resources.getQuantityString(R.plurals.days_count, days, days)

        val text = if (name.isNotEmpty()) {
            context.getString(R.string.notification_text_with_name, name, daysString)
        } else {
            context.getString(R.string.notification_text_no_name, daysString)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "tracker_reminder_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)

        // Перезапускаем будильник
        NotificationHelper.scheduleDailyReminder(context)
    }
}