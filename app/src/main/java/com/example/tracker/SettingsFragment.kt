package com.example.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.tracker.utils.PreferencesManager

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
        val spinnerAddiction = view.findViewById<Spinner>(R.id.spinnerSettingsAddiction) // Наш Spinner
        val switchNotif = view.findViewById<Switch>(R.id.switchNotifications)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupTheme)
        val btnSave = view.findViewById<Button>(R.id.btnSaveSettings)
        val btnManageData = view.findViewById<Button>(R.id.btnManageData)

        // Настраиваем Spinner (Выпадающий список)
        val addictionTypes = arrayOf("Курение", "Алкоголь", "Оба")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, addictionTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAddiction.adapter = spinnerAdapter

        // Заполняем текущими значениями
        etName.setText(prefs.getUserName())
        etBudget.setText(prefs.getDailyBudget().toString())
        switchNotif.isChecked = prefs.areNotificationsEnabled()

        // Устанавливаем сохраненный тип зависимости в Spinner
        val savedType = prefs.getAddictionType()
        val typePosition = addictionTypes.indexOf(savedType)
        if (typePosition >= 0) {
            spinnerAddiction.setSelection(typePosition)
        }

        when (prefs.getTheme()) {
            1 -> radioGroup.check(R.id.radioDark)
            2 -> radioGroup.check(R.id.radioSystem)
            else -> radioGroup.check(R.id.radioLight)
        }

        // ОБРАБОТЧИК КНОПКИ "СОХРАНИТЬ"
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val budgetStr = etBudget.text.toString().trim()
            val selectedAddiction = spinnerAddiction.selectedItem.toString() // Берем из списка

            if (name.isNotEmpty() && budgetStr.isNotEmpty()) {
                prefs.setUserName(name)
                prefs.setDailyBudget(budgetStr.toFloatOrNull() ?: 300f)
                prefs.setAddictionType(selectedAddiction) // Сохраняем зависимость
                prefs.setNotificationsEnabled(switchNotif.isChecked)

                val selectedTheme = when(radioGroup.checkedRadioButtonId) {
                    R.id.radioDark -> 1
                    R.id.radioSystem -> 2
                    else -> 0
                }
                prefs.setTheme(selectedTheme)

                // Применяем тему сразу
                when (selectedTheme) {
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                Toast.makeText(requireContext(), "Настройки сохранены!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        // ОБРАБОТЧИК КНОПКИ "УПРАВЛЕНИЕ БАЗОЙ"
        btnManageData.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.manageDataFragment)
        }
    }
}