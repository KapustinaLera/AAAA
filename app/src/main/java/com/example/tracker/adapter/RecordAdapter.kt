package com.example.tracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tracker.R
import com.example.tracker.data.HistoryRecord
import com.example.tracker.utils.ProgressRingView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordAdapter(
    private val onEditClick: (HistoryRecord) -> Unit,
    private val onDeleteClick: (HistoryRecord) -> Unit
) : ListAdapter<HistoryRecord, RecordAdapter.RecordViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
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
        private val ivRecordPhoto: ImageView = itemView.findViewById(R.id.ivRecordPhoto)
        private val ringRecord: ProgressRingView = itemView.findViewById(R.id.ringRecord)

        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(item: HistoryRecord, onEdit: (HistoryRecord) -> Unit, onDelete: (HistoryRecord) -> Unit) {
            val record = item.record
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            tvDate.text = dateFormat.format(Date(record.date))

            val context = itemView.context
            tvMood.text = context.getString(R.string.mood_format, record.mood)
            tvCraving.text = context.getString(R.string.craving_format, record.cravingLevel)
            tvWellBeing.text = context.getString(R.string.well_being_format, record.wellBeing)

            val amountText = record.consumedAmount
            val moneyText = if (record.moneySpent > 0) " | Потрачено: ${record.moneySpent} руб." else ""
            tvAmount.text = context.getString(R.string.amount_format, amountText) + moneyText

            tvSleep.text = context.getString(R.string.sleep_format, record.sleepQuality)
            tvActivity.text = if (record.physicalActivity) context.getString(R.string.activity_yes) else context.getString(R.string.activity_no)
            tvNotes.text = if (record.notes.isNotEmpty()) context.getString(R.string.notes_format, record.notes) else context.getString(R.string.no_notes)

            if (record.photoPath != null) {
                ivRecordPhoto.visibility = View.VISIBLE
                Glide.with(context).load(record.photoPath).into(ivRecordPhoto)
            } else {
                ivRecordPhoto.visibility = View.GONE
            }

            // НАСТРОЙКА КОЛЬЦА С УЧЕТОМ ЦЕЛИ ПОЛЬЗОВАТЕЛЯ
            val maxGoal = item.targetDays.toFloat()
            if (item.isRelapse) {
                ringRecord.setRingColor(android.graphics.Color.RED)
                ringRecord.setProgress(0f, maxGoal, animate = false)
            } else {
                ringRecord.setRingColor(android.graphics.Color.parseColor("#4CAF50"))
                ringRecord.setProgress(item.streak.toFloat().coerceAtMost(maxGoal), maxGoal, animate = true)
            }

            btnEdit.setOnClickListener { onEdit(item) }
            btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<HistoryRecord>() {
        override fun areItemsTheSame(oldItem: HistoryRecord, newItem: HistoryRecord): Boolean {
            return oldItem.record.id == newItem.record.id
        }

        override fun areContentsTheSame(oldItem: HistoryRecord, newItem: HistoryRecord): Boolean {
            return oldItem == newItem
        }
    }
}