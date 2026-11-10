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
import com.example.tracker.data.User

class UserAdapter(
    private val onEdit: (User) -> Unit,
    private val onDelete: (User) -> Unit
) : ListAdapter<User, UserAdapter.VH>(Diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return VH(view)
    }
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position), onEdit, onDelete)

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvInfo: TextView = itemView.findViewById(R.id.tvUserInfo)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEditUser)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDeleteUser)

        fun bind(user: User, onEdit: (User) -> Unit, onDelete: (User) -> Unit) {
            tvName.text = user.name
            tvInfo.text = "Тип: ${user.addictionType}, Бюджет: ${user.dailyBudget}"
            btnEdit.setOnClickListener { onEdit(user) }
            btnDelete.setOnClickListener { onDelete(user) }
        }
    }
    companion object Diff : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(o: User, n: User) = o.id == n.id
        override fun areContentsTheSame(o: User, n: User) = o == n
    }
}