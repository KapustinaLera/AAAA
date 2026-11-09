package com.example.tracker.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tracker.R
import com.example.tracker.data.DayRecord
import com.example.tracker.viewmodel.TrackerViewModel
import java.util.Calendar

class AddRecordFragment : Fragment() {

    private val viewModel: TrackerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etCravingLevel = view.findViewById<EditText>(R.id.etCravingLevel)
        val etConsumedAmount = view.findViewById<EditText>(R.id.etConsumedAmount)
        val etWellBeing = view.findViewById<EditText>(R.id.etWellBeing)
        val etMood = view.findViewById<EditText>(R.id.etMood)
        val etSleepQuality = view.findViewById<EditText>(R.id.etSleepQuality)
        val cbPhysicalActivity = view.findViewById<CheckBox>(R.id.cbPhysicalActivity)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            // Получаем данные из полей
            val cravingLevel = etCravingLevel.text.toString().toIntOrNull()
            val consumedAmount = etConsumedAmount.text.toString().toIntOrNull()
            val wellBeing = etWellBeing.text.toString().toIntOrNull()
            val mood = etMood.text.toString()
            val sleepQuality = etSleepQuality.text.toString().toIntOrNull()
            val physicalActivity = cbPhysicalActivity.isChecked
            val notes = etNotes.text.toString()

            // Проверяем, что все числовые поля заполнены
            if (cravingLevel == null || consumedAmount == null ||
                wellBeing == null || sleepQuality == null) {
                Toast.makeText(requireContext(), "Заполните все числовые поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверяем диапазоны
            if (cravingLevel !in 1..10 || wellBeing !in 1..10 || sleepQuality !in 1..10) {
                Toast.makeText(requireContext(), "Числа должны быть от 1 до 10", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Создаём запись с текущей датой
            val record = DayRecord(
                date = Calendar.getInstance().timeInMillis,
                cravingLevel = cravingLevel,
                consumedAmount = consumedAmount,
                wellBeing = wellBeing,
                mood = if (mood.isNotEmpty()) mood else "Не указано",
                sleepQuality = sleepQuality,
                physicalActivity = physicalActivity,
                notes = notes,
                photoPath = null
            )

            // Сохраняем в базу
            viewModel.insertRecord(record)

            // Показываем сообщение об успехе
            Toast.makeText(requireContext(), "Запись сохранена!", Toast.LENGTH_SHORT).show()

            // Очищаем поля
            etCravingLevel.text.clear()
            etConsumedAmount.text.clear()
            etWellBeing.text.clear()
            etMood.text.clear()
            etSleepQuality.text.clear()
            cbPhysicalActivity.isChecked = false
            etNotes.text.clear()
        }
    }
}