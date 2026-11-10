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
import com.example.tracker.data.HealthMilestone

class MilestoneAdapter(
    private val onEdit: (HealthMilestone) -> Unit,
    private val onDelete: (HealthMilestone) -> Unit
) : ListAdapter<HealthMilestone, MilestoneAdapter.VH>(Diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_milestone, parent, false)
        return VH(view)
    }
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position), onEdit, onDelete)

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDays: TextView = itemView.findViewById(R.id.tvMilestoneDays)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvMilestoneDesc)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEditMilestone)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDeleteMilestone)

        fun bind(item: HealthMilestone, onEdit: (HealthMilestone) -> Unit, onDelete: (HealthMilestone) -> Unit) {
            tvDays.text = "${item.daysCount} дней"
            tvDesc.text = item.description
            btnEdit.setOnClickListener { onEdit(item) }
            btnDelete.setOnClickListener { onDelete(item) }
        }
    }
    companion object Diff : DiffUtil.ItemCallback<HealthMilestone>() {
        override fun areItemsTheSame(o: HealthMilestone, n: HealthMilestone) = o.id == n.id
        override fun areContentsTheSame(o: HealthMilestone, n: HealthMilestone) = o == n
    }
}