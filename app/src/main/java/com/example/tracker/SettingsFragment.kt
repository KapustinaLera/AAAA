package com.example.tracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.tracker.utils.NotificationHelper
import com.example.tracker.utils.PreferencesManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SettingsFragment : Fragment() {
    private lateinit var prefs: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PreferencesManager(requireContext())

        val etName = view.findViewById<EditText>(R.id.etSettingsName)
        val etBudget = view.findViewById<EditText>(R.id.etSettingsBudget)
        val etTargetDays = view.findViewById<EditText>(R.id.etTargetDays) // <-- НОВОЕ ПОЛЕ
        val spinnerAddiction = view.findViewById<Spinner>(R.id.spinnerSettingsAddiction)
        val switchNotif = view.findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.switchNotifications)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupTheme)
        val btnSave = view.findViewById<Button>(R.id.btnSaveSettings)
        val btnPickTime = view.findViewById<Button>(R.id.btnPickTime)
        val btnTestNotification = view.findViewById<Button>(R.id.btnTestNotification)
        val btnPickQuitDate = view.findViewById<Button>(R.id.btnPickQuitDate)

        val addictionTypes = arrayOf("Курение", "Алкоголь", "Оба")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, addictionTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAddiction.adapter = spinnerAdapter

        // Заполняем текущими значениями
        etName.setText(prefs.getUserName())
        etBudget.setText(prefs.getDailyBudget().toString())
        etTargetDays.setText(prefs.getTargetDays().toString()) // <-- ЗАГРУЖАЕМ ЦЕЛЬ
        switchNotif.isChecked = prefs.areNotificationsEnabled()

        fun updateTimeText() {
            val h = prefs.getNotificationHour().toString().padStart(2, '0')
            val m = prefs.getNotificationMinute().toString().padStart(2, '0')
            btnPickTime.text = getString(R.string.notification_time, h, m)
        }
        updateTimeText()

        btnPickTime.setOnClickListener {
            val hour = prefs.getNotificationHour()
            val minute = prefs.getNotificationMinute()
            TimePickerDialog(requireContext(), { _, h, m ->
                prefs.setNotificationTime(h, m)
                updateTimeText()
                NotificationHelper.scheduleDailyReminder(requireContext())
            }, hour, minute, true).show()
        }

        btnTestNotification.setOnClickListener {
            val intent = Intent(requireContext(), ReminderReceiver::class.java)
            requireContext().sendBroadcast(intent)
            Toast.makeText(requireContext(), "Уведомление отправлено!", Toast.LENGTH_SHORT).show()
        }

        val savedType = prefs.getAddictionType()
        val typePosition = addictionTypes.indexOf(savedType)
        if (typePosition >= 0) spinnerAddiction.setSelection(typePosition)

        when (prefs.getTheme()) {
            1 -> radioGroup.check(R.id.radioDark)
            2 -> radioGroup.check(R.id.radioSystem)
            else -> radioGroup.check(R.id.radioLight)
        }

        fun updateQuitDateText() {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            btnPickQuitDate.text = "Дата отказа: " + sdf.format(Date(prefs.getQuitDate()))
        }
        updateQuitDateText()

        btnPickQuitDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = prefs.getQuitDate()

            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val newDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                prefs.setQuitDate(newDate.timeInMillis)
                updateQuitDateText()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val budgetStr = etBudget.text.toString().trim()
            val targetDaysStr = etTargetDays.text.toString().trim()
            val selectedAddiction = spinnerAddiction.selectedItem.toString()

            if (name.isNotEmpty() && budgetStr.isNotEmpty() && targetDaysStr.isNotEmpty()) {
                prefs.setUserName(name)
                prefs.setDailyBudget(budgetStr.toFloatOrNull() ?: 300f)
                prefs.setTargetDays(targetDaysStr.toIntOrNull() ?: 30) // <-- СОХРАНЯЕМ ЦЕЛЬ
                prefs.setAddictionType(selectedAddiction)
                prefs.setNotificationsEnabled(switchNotif.isChecked)

                val selectedTheme = when(radioGroup.checkedRadioButtonId) {
                    R.id.radioDark -> 1
                    R.id.radioSystem -> 2
                    else -> 0
                }
                prefs.setTheme(selectedTheme)

                when (selectedTheme) {
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                if (switchNotif.isChecked) NotificationHelper.scheduleDailyReminder(requireContext())

                Toast.makeText(requireContext(), "Настройки сохранены!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }
}