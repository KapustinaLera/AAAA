package com.example.tracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tracker.R
import com.example.tracker.data.DayRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordAdapter : ListAdapter<DayRecord, RecordAdapter.RecordViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvMood: TextView = itemView.findViewById(R.id.tvMood)
        private val tvCraving: TextView = itemView.findViewById(R.id.tvCraving)
        private val tvWellBeing: TextView = itemView.findViewById(R.id.tvWellBeing)
        private val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)

        fun bind(record: DayRecord) {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            tvDate.text = dateFormat.format(Date(record.date))
            tvMood.text = "Настроение: ${record.mood}"
            tvCraving.text = "Тяга: ${record.cravingLevel}/10"
            tvWellBeing.text = "Самочувствие: ${record.wellBeing}/10"
            tvNotes.text = if (record.notes.isNotEmpty()) record.notes else "Без заметок"
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DayRecord>() {
        override fun areItemsTheSame(oldItem: DayRecord, newItem: DayRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DayRecord, newItem: DayRecord): Boolean {
            return oldItem == newItem
        }
    }
}