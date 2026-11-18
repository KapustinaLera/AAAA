package com.example.tracker.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tracker_prefs", Context.MODE_PRIVATE)

    fun getUserName(): String = prefs.getString("user_name", "") ?: ""
    fun setUserName(name: String) = prefs.edit().putString("user_name", name).apply()

    fun getQuitDate(): Long = prefs.getLong("quit_date", System.currentTimeMillis())
    fun setQuitDate(date: Long) = prefs.edit().putLong("quit_date", date).apply()

    fun getDailyBudget(): Float = prefs.getFloat("daily_budget", 300f)
    fun setDailyBudget(budget: Float) = prefs.edit().putFloat("daily_budget", budget).apply()

    fun getAddictionType(): String = prefs.getString("addiction_type", "Курение") ?: "Курение"
    fun setAddictionType(type: String) = prefs.edit().putString("addiction_type", type).apply()

    fun areNotificationsEnabled(): Boolean = prefs.getBoolean("notifications_enabled", true)
    fun setNotificationsEnabled(enabled: Boolean) = prefs.edit().putBoolean("notifications_enabled", enabled).apply()

    fun getTheme(): Int = prefs.getInt("app_theme", 0)
    fun setTheme(theme: Int) = prefs.edit().putInt("app_theme", theme).apply()

    fun getNotificationHour(): Int = prefs.getInt("notif_hour", 9)
    fun getNotificationMinute(): Int = prefs.getInt("notif_minute", 0)
    fun setNotificationTime(hour: Int, minute: Int) {
        prefs.edit().putInt("notif_hour", hour).putInt("notif_minute", minute).apply()
    }

    fun getLastRelapseDate(): Long = prefs.getLong("last_relapse_date", getQuitDate())
    fun setLastRelapseDate(date: Long) = prefs.edit().putLong("last_relapse_date", date).apply()

    // НОВОЕ: Целевое количество дней
    fun getTargetDays(): Int = prefs.getInt("target_days", 30)
    fun setTargetDays(days: Int) = prefs.edit().putInt("target_days", days).apply()
}