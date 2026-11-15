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

            // Используем getString() из контекста
            val context = itemView.context
            tvMood.text = context.getString(R.string.mood_format, record.mood)
            tvCraving.text = context.getString(R.string.craving_format, record.cravingLevel)
            tvWellBeing.text = context.getString(R.string.well_being_format, record.wellBeing)
            tvAmount.text = context.getString(R.string.amount_format, record.consumedAmount)
            tvSleep.text = context.getString(R.string.sleep_format, record.sleepQuality)
            tvActivity.text = if (record.physicalActivity) context.getString(R.string.activity_yes) else context.getString(R.string.activity_no)
            tvNotes.text = if (record.notes.isNotEmpty()) context.getString(R.string.notes_format, record.notes) else context.getString(R.string.no_notes)

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