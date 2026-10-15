package com.example.hw04

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
    private val students: List<Student>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private var selectedIndex = -1

    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        val tvStudentId: TextView = view.findViewById(R.id.tvStudentId)
    }

    fun setSelected(newIndex: Int) {
        if (newIndex == selectedIndex) return
        val old = selectedIndex
        selectedIndex = newIndex
        if (old in students.indices) notifyItemChanged(old)
        if (newIndex in students.indices) notifyItemChanged(newIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        val holder = StudentViewHolder(view)
        view.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) onItemClick(pos)
        }
        return holder
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val s = students[position]
        holder.imgAvatar.setImageResource(s.avatarRes)
        holder.tvStudentId.text = s.id
        holder.itemView.isActivated = (position == selectedIndex)
    }

    override fun getItemCount(): Int = students.size
}