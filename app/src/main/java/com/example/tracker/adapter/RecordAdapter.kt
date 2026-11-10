package com.example.tracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tracker.R
import com.example.tracker.data.DayRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordAdapter(
    private val onEditClick: (DayRecord) -> Unit,
    private val onDeleteClick: (DayRecord) -> Unit
) : ListAdapter<DayRecord, RecordAdapter.RecordViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(getItem(position), onEditClick, onDeleteClick)
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvMood: TextView = itemView.findViewById(R.id.tvMood)
        private val tvCraving: TextView = itemView.findViewById(R.id.tvCraving)
        private val tvWellBeing: TextView = itemView.findViewById(R.id.tvWellBeing)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvSleep: TextView = itemView.findViewById(R.id.tvSleep)
        private val tvActivity: TextView = itemView.findViewById(R.id.tvActivity)
        private val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)

        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(record: DayRecord, onEdit: (DayRecord) -> Unit, onDelete: (DayRecord) -> Unit) {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            tvDate.text = dateFormat.format(Date(record.date))
            tvMood.text = "Настроение: ${record.mood}"
            tvCraving.text = "Тяга: ${record.cravingLevel}/10"
            tvWellBeing.text = "Самочувствие: ${record.wellBeing}/10"
            tvAmount.text = "Выпито/выкурено: ${record.consumedAmount}"
            tvSleep.text = "Сон: ${record.sleepQuality}/10"
            tvActivity.text = if (record.physicalActivity) "Активность: Была" else "Активность: Не было"
            tvNotes.text = if (record.notes.isNotEmpty()) "Заметка: ${record.notes}" else "Без заметок"

            btnEdit.setOnClickListener { onEdit(record) }
            btnDelete.setOnClickListener { onDelete(record) }
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