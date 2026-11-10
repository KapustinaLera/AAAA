package com.example.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tracker.utils.PreferencesManager

class HomeFragment : Fragment() {

    private lateinit var prefs: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PreferencesManager(requireContext())

        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        val layoutNameInput = view.findViewById<LinearLayout>(R.id.layoutNameInput)
        val layoutStats = view.findViewById<LinearLayout>(R.id.layoutStats)
        val etUserName = view.findViewById<EditText>(R.id.etUserName)
        val btnSaveName = view.findViewById<Button>(R.id.btnSaveName)

        val tvDays = view.findViewById<TextView>(R.id.tvDaysWithout)
        val tvMoney = view.findViewById<TextView>(R.id.tvMoneySaved)

        fun updateUI() {
            val name = prefs.getUserName()
            if (name.isEmpty()) {
                // Если имени нет — показываем знакомство, прячем статистику
                tvGreeting.text = "Привет!"
                layoutNameInput.visibility = View.VISIBLE
                layoutStats.visibility = View.GONE
            } else {
                // Если имя есть — показываем статистику
                tvGreeting.text = "Привет, $name! 👋"
                layoutNameInput.visibility = View.GONE
                layoutStats.visibility = View.VISIBLE

                val days = ((System.currentTimeMillis() - prefs.getQuitDate()) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
                tvDays.text = "Дней без привычки: $days"
                tvMoney.text = "Сэкономлено: ${days * prefs.getDailyBudget()} руб."
            }
        }

        updateUI() // Вызываем при старте

        btnSaveName.setOnClickListener {
            val newName = etUserName.text.toString().trim()
            if (newName.isNotEmpty()) {
                prefs.setUserName(newName)
                updateUI() // Обновляем экран, чтобы показать статистику
                Toast.makeText(requireContext(), "Приятно познакомиться, $newName!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Введите имя", Toast.LENGTH_SHORT).show()
            }
        }
    }
}