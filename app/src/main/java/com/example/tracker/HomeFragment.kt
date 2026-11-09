package com.example.tracker.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tracker.R
import com.example.tracker.utils.PreferencesManager

class HomeFragment : Fragment() {

    private lateinit var prefsManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем наш "блокнот"
        prefsManager = PreferencesManager(requireContext())

        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        val etUserName = view.findViewById<EditText>(R.id.etUserName)
        val btnSaveName = view.findViewById<Button>(R.id.btnSaveName)

        // 1. При открытии экрана читаем имя из блокнота и показываем его
        val savedName = prefsManager.getUserName()
        tvGreeting.text = "Привет, $savedName!"
        etUserName.setText(savedName)

        // 2. При нажатии на кнопку сохраняем новое имя
        btnSaveName.setOnClickListener {
            val newName = etUserName.text.toString().trim()
            if (newName.isNotEmpty()) {
                prefsManager.setUserName(newName) // Сохраняем в SharedPreferences
                tvGreeting.text = "Привет, $newName!" // Сразу обновляем текст
                Toast.makeText(requireContext(), "Имя сохранено!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
            }
        }
    }
}