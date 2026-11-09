package com.example.tracker.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    // Создаём "блокнот" с именем tracker_prefs
    private val prefs: SharedPreferences = context.getSharedPreferences("tracker_prefs", Context.MODE_PRIVATE)

    // Функция: прочитать имя
    fun getUserName(): String {
        return prefs.getString("user_name", "Незнакомец") ?: "Незнакомец"
    }

    // Функция: сохранить имя
    fun setUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
    }
}