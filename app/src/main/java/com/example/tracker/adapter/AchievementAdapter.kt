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
import com.example.tracker.data.Achievement

class AchievementAdapter(
    private val onEdit: (Achievement) -> Unit,
    private val onDelete: (Achievement) -> Unit
) : ListAdapter<Achievement, AchievementAdapter.VH>(Diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_achievement, parent, false)
        return VH(view)
    }
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position), onEdit, onDelete)

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvType: TextView = itemView.findViewById(R.id.tvAchType)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEditAch)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDeleteAch)

        fun bind(item: Achievement, onEdit: (Achievement) -> Unit, onDelete: (Achievement) -> Unit) {
            tvType.text = "Достижение: ${item.type}"
            btnEdit.setOnClickListener { onEdit(item) }
            btnDelete.setOnClickListener { onDelete(item) }
        }
    }
    companion object Diff : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(o: Achievement, n: Achievement) = o.id == n.id
        override fun areContentsTheSame(o: Achievement, n: Achievement) = o == n
    }
}