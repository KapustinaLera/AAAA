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

        // Находим все элементы
        val etCravingLevel = view.findViewById<EditText>(R.id.etCravingLevel)
        val etWellBeing = view.findViewById<EditText>(R.id.etWellBeing)
        val etMood = view.findViewById<EditText>(R.id.etMood)
        val etSleepQuality = view.findViewById<EditText>(R.id.etSleepQuality)
        val cbPhysicalActivity = view.findViewById<CheckBox>(R.id.cbPhysicalActivity)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Блоки для курения и алкоголя
        val layoutSmoking = view.findViewById<LinearLayout>(R.id.layoutSmoking)
        val layoutAlcohol = view.findViewById<LinearLayout>(R.id.layoutAlcohol)
        val etSmokedAmount = view.findViewById<EditText>(R.id.etSmokedAmount)
        val etAlcoholAmount = view.findViewById<EditText>(R.id.etAlcoholAmount)
        val spinnerSmokingUnit = view.findViewById<Spinner>(R.id.spinnerSmokingUnit)
        val spinnerAlcoholUnit = view.findViewById<Spinner>(R.id.spinnerAlcoholUnit)

        // Настраиваем выпадающие списки (единицы измерения)
        val smokingUnits = arrayOf("шт", "пачка")
        spinnerSmokingUnit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, smokingUnits)

        val alcoholUnits = arrayOf("мл", "рюмок", "бокалов", "бутылок")
        spinnerAlcoholUnit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, alcoholUnits)

        // Смотрим, какая зависимость в настройках
        val prefs = PreferencesManager(requireContext())
        when (prefs.getAddictionType()) {
            "Курение" -> layoutSmoking.visibility = View.VISIBLE
            "Алкоголь" -> layoutAlcohol.visibility = View.VISIBLE
            "Оба" -> {
                layoutSmoking.visibility = View.VISIBLE
                layoutAlcohol.visibility = View.VISIBLE
            }
        }

        recordIdToEdit = arguments?.getInt("recordId", 0) ?: 0

        // Если редактируем, заполняем поля (количество просто выводим в текстовом виде)
        if (recordIdToEdit > 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                val existingRecord = viewModel.getRecordById(recordIdToEdit)
                existingRecord?.let { r ->
                    etCravingLevel.setText(r.cravingLevel.toString())
                    etWellBeing.setText(r.wellBeing.toString())
                    etMood.setText(r.mood)
                    etSleepQuality.setText(r.sleepQuality.toString())
                    cbPhysicalActivity.isChecked = r.physicalActivity
                    etNotes.setText(r.notes)

                    // Для простоты при редактировании пихаем текст в первое попавшееся поле
                    if (layoutSmoking.visibility == View.VISIBLE) {
                        etSmokedAmount.setText(r.consumedAmount)
                    } else if (layoutAlcohol.visibility == View.VISIBLE) {
                        etAlcoholAmount.setText(r.consumedAmount)
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            val cravingLevel = etCravingLevel.text.toString().toIntOrNull()
            val wellBeing = etWellBeing.text.toString().toIntOrNull()
            val sleepQuality = etSleepQuality.text.toString().toIntOrNull()

            if (cravingLevel == null || wellBeing == null || sleepQuality == null) {
                Toast.makeText(requireContext(), "Заполните все числовые поля (1-10)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // СОБИРАЕМ СТРОКУ С КОЛИЧЕСТВОМ
            var consumedText = "Не указано"
            val smokedVal = etSmokedAmount.text.toString()
            val alcoholVal = etAlcoholAmount.text.toString()
            val smokedUnit = spinnerSmokingUnit.selectedItem.toString()
            val alcoholUnit = spinnerAlcoholUnit.selectedItem.toString()

            when (prefs.getAddictionType()) {
                "Курение" -> if (smokedVal.isNotEmpty()) consumedText = "$smokedVal $smokedUnit"
                "Алкоголь" -> if (alcoholVal.isNotEmpty()) consumedText = "$alcoholVal $alcoholUnit"
                "Оба" -> {
                    val parts = mutableListOf<String>()
                    if (smokedVal.isNotEmpty()) parts.add("$smokedVal $smokedUnit сигарет")
                    if (alcoholVal.isNotEmpty()) parts.add("$alcoholVal $alcoholUnit алкоголя")
                    if (parts.isNotEmpty()) consumedText = parts.joinToString(", ")
                }
            }

            val record = DayRecord(
                id = recordIdToEdit,
                date = if (recordIdToEdit > 0) System.currentTimeMillis() else Calendar.getInstance().timeInMillis,
                cravingLevel = cravingLevel,
                consumedAmount = consumedText, // <-- СОХРАНЯЕМ ТЕКСТОВУЮ СТРОКУ
                wellBeing = wellBeing,
                mood = if (etMood.text.isNotEmpty()) etMood.text.toString() else "Не указано",
                sleepQuality = sleepQuality,
                physicalActivity = cbPhysicalActivity.isChecked,
                notes = etNotes.text.toString(),
                photoPath = null
            )

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