package com.example.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracker.adapter.RecordAdapter
import com.example.tracker.data.DayRecord
import com.example.tracker.data.HistoryRecord
import com.example.tracker.utils.PreferencesManager
import com.example.tracker.viewmodel.TrackerViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

class ProgressFragment : Fragment() {

    private val viewModel: TrackerViewModel by viewModels()
    private lateinit var adapter: RecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val prefs = PreferencesManager(requireContext())

        adapter = RecordAdapter(
            onEditClick = { item ->
                val bundle = Bundle().apply { putInt("recordId", item.record.id) }
                NavHostFragment.findNavController(this).navigate(R.id.addRecordFragment, bundle)
            },
            onDeleteClick = { item ->
                viewModel.deleteRecord(item.record)
                Toast.makeText(requireContext(), "Запись удалена", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllRecords().collect { records ->
                val historyList = calculateHistory(records, prefs.getQuitDate(), prefs.getTargetDays())
                adapter.submitList(historyList)
            }
        }
    }

    private fun calculateHistory(records: List<DayRecord>, quitDate: Long, targetDays: Int): List<HistoryRecord> {
        val sortedAsc = records.sortedBy { it.date }
        val history = mutableListOf<HistoryRecord>()

        // Обнуляем время стартовой даты
        var lastRelapseDate = getStartOfDay(quitDate)

        for (record in sortedAsc) {
            val isRelapse = record.amountSmoked > 0 || record.amountDrunk > 0
            val recordDay = getStartOfDay(record.date)

            // Считаем чистые дни до этой записи
            val streak = ((recordDay - lastRelapseDate) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)

            if (isRelapse) {
                history.add(HistoryRecord(record, 0, true, targetDays))
                lastRelapseDate = recordDay // Сбрасываем счетчик на этот день
            } else {
                history.add(HistoryRecord(record, streak, false, targetDays))
            }
        }

        return history.reversed() // Новые записи сверху
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}