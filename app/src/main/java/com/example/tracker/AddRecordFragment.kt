package com.example.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.tracker.data.DayRecord
import com.example.tracker.utils.PreferencesManager
import com.example.tracker.viewmodel.TrackerViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

class AddRecordFragment : Fragment() {

    private val viewModel: TrackerViewModel by viewModels()
    private var recordIdToEdit: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvCravingLabel = view.findViewById<TextView>(R.id.tvCravingLabel)
        val sbCravingLevel = view.findViewById<SeekBar>(R.id.sbCravingLevel)
        val tvWellBeingLabel = view.findViewById<TextView>(R.id.tvWellBeingLabel)
        val sbWellBeing = view.findViewById<SeekBar>(R.id.sbWellBeing)
        val tvSleepLabel = view.findViewById<TextView>(R.id.tvSleepLabel)
        val sbSleepQuality = view.findViewById<SeekBar>(R.id.sbSleepQuality)

        val etMoneySpent = view.findViewById<EditText>(R.id.etMoneySpent)
        val etMood = view.findViewById<EditText>(R.id.etMood)
        val cbPhysicalActivity = view.findViewById<CheckBox>(R.id.cbPhysicalActivity)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        val layoutSmoking = view.findViewById<LinearLayout>(R.id.layoutSmoking)
        val layoutAlcohol = view.findViewById<LinearLayout>(R.id.layoutAlcohol)
        val etSmokedAmount = view.findViewById<EditText>(R.id.etSmokedAmount)
        val etAlcoholAmount = view.findViewById<EditText>(R.id.etAlcoholAmount)

        val prefs = PreferencesManager(requireContext())
        when (prefs.getAddictionType()) {
            "Курение" -> layoutSmoking.visibility = View.VISIBLE
            "Алкоголь" -> layoutAlcohol.visibility = View.VISIBLE
            "Оба" -> {
                layoutSmoking.visibility = View.VISIBLE
                layoutAlcohol.visibility = View.VISIBLE
            }
        }

        // НАСТРОЙКА ПОЛЗУНКОВ
        sbCravingLevel.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvCravingLabel.text = "Уровень тяги: ${progress + 1}/10"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sbWellBeing.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvWellBeingLabel.text = "Самочувствие: ${progress + 1}/10"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sbSleepQuality.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSleepLabel.text = "Качество сна: ${progress + 1}/10"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        recordIdToEdit = arguments?.getInt("recordId", 0) ?: 0

        // ЕСЛИ РЕДАКТИРУЕМ, ЗАПОЛНЯЕМ ПОЛЯ
        if (recordIdToEdit > 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                val existingRecord = viewModel.getRecordById(recordIdToEdit)
                existingRecord?.let { r ->
                    sbCravingLevel.progress = r.cravingLevel - 1
                    sbWellBeing.progress = r.wellBeing - 1
                    sbSleepQuality.progress = r.sleepQuality - 1
                    etMoneySpent.setText(r.moneySpent.toString())
                    etMood.setText(r.mood)
                    cbPhysicalActivity.isChecked = r.physicalActivity
                    etNotes.setText(r.notes)

                    // ИСПРАВЛЕНО: Берем точные числа из базы, а не вытаскиваем цифры из текста
                    if (layoutSmoking.visibility == View.VISIBLE) etSmokedAmount.setText(r.amountSmoked.toString())
                    if (layoutAlcohol.visibility == View.VISIBLE) etAlcoholAmount.setText(r.amountDrunk.toString())
                }
            }
        }

        btnSave.setOnClickListener {
            // 1. Получаем значения ползунков (от 0 до 9, поэтому +1)
            val cravingLevel = sbCravingLevel.progress + 1
            val wellBeing = sbWellBeing.progress + 1
            val sleepQuality = sbSleepQuality.progress + 1
            val moneySpent = etMoneySpent.text.toString().toDoubleOrNull() ?: 0.0

            // 2. Получаем числа из полей ввода
            val smokedVal = etSmokedAmount.text.toString()
            val alcoholVal = etAlcoholAmount.text.toString()

            val amountSmoked = if (prefs.getAddictionType() == "Курение" || prefs.getAddictionType() == "Оба") {
                smokedVal.toIntOrNull() ?: 0
            } else 0

            val amountDrunk = if (prefs.getAddictionType() == "Алкоголь" || prefs.getAddictionType() == "Оба") {
                alcoholVal.toIntOrNull() ?: 0
            } else 0

            // 3. Собираем красивый текст для отображения
            var consumedText = "Не указано"
            when (prefs.getAddictionType()) {
                "Курение" -> if (smokedVal.isNotEmpty()) consumedText = "$smokedVal шт."
                "Алкоголь" -> if (alcoholVal.isNotEmpty()) consumedText = "$alcoholVal мл"
                "Оба" -> {
                    val parts = mutableListOf<String>()
                    if (smokedVal.isNotEmpty()) parts.add("$smokedVal шт. сигарет")
                    if (alcoholVal.isNotEmpty()) parts.add("$alcoholVal мл алкоголя")
                    if (parts.isNotEmpty()) consumedText = parts.joinToString(", ")
                }
            }

            // 4. Создаем ОДНУ запись со всеми полями
            val record = DayRecord(
                id = recordIdToEdit,
                date = if (recordIdToEdit > 0) System.currentTimeMillis() else Calendar.getInstance().timeInMillis,
                cravingLevel = cravingLevel,
                consumedAmount = consumedText,
                amountSmoked = amountSmoked,
                amountDrunk = amountDrunk,
                moneySpent = moneySpent,
                wellBeing = wellBeing,
                mood = if (etMood.text.isNotEmpty()) etMood.text.toString() else "Не указано",
                sleepQuality = sleepQuality,
                physicalActivity = cbPhysicalActivity.isChecked,
                notes = etNotes.text.toString(),
                photoPath = null
            )

            // 5. Сохраняем и закрываем экран
            if (recordIdToEdit > 0) {
                viewModel.updateRecord(record)
                Toast.makeText(requireContext(), "Запись обновлена!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insertRecord(record)
                Toast.makeText(requireContext(), "Запись сохранена!", Toast.LENGTH_SHORT).show()
            }

            NavHostFragment.findNavController(this).popBackStack()
        }
    }
}