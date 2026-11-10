package com.example.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracker.adapter.RecordAdapter
import com.example.tracker.viewmodel.TrackerViewModel
import kotlinx.coroutines.launch

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

        adapter = RecordAdapter(
            onEditClick = { record ->
                // Переходим на экран добавления, передавая ID записи
                val bundle = Bundle().apply { putInt("recordId", record.id) }
                androidx.navigation.fragment.NavHostFragment.findNavController(this)
                    .navigate(R.id.addRecordFragment, bundle)
            },
            onDeleteClick = { record ->
                viewModel.deleteRecord(record)
                Toast.makeText(requireContext(), "Запись удалена", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllRecords().collect { records ->
                adapter.submitList(records)
            }
        }
    }
}