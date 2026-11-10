package com.example.tracker.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tracker_prefs", Context.MODE_PRIVATE)

    // Имя
    fun getUserName(): String = prefs.getString("user_name", "") ?: ""
    fun setUserName(name: String) = prefs.edit().putString("user_name", name).apply()

    // Дата отказа
    fun getQuitDate(): Long = prefs.getLong("quit_date", System.currentTimeMillis())
    fun setQuitDate(date: Long) = prefs.edit().putLong("quit_date", date).apply()

    // Бюджет
    fun getDailyBudget(): Float = prefs.getFloat("daily_budget", 300f)
    fun setDailyBudget(budget: Float) = prefs.edit().putFloat("daily_budget", budget).apply()

    // Тип зависимости (по умолчанию "Курение")
    fun getAddictionType(): String = prefs.getString("addiction_type", "Курение") ?: "Курение"
    fun setAddictionType(type: String) = prefs.edit().putString("addiction_type", type).apply()

    // Настройки уведомлений
    fun areNotificationsEnabled(): Boolean = prefs.getBoolean("notifications_enabled", true)
    fun setNotificationsEnabled(enabled: Boolean) = prefs.edit().putBoolean("notifications_enabled", enabled).apply()

    // Настройки темы (0 - светлая, 1 - темная, 2 - системная)
    fun getTheme(): Int = prefs.getInt("app_theme", 0)
    fun setTheme(theme: Int) = prefs.edit().putInt("app_theme", theme).apply()
}