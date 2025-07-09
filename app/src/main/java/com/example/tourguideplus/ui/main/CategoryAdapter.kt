package com.example.tourguideplus.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tourguideplus.R
import com.example.tourguideplus.data.model.CategoryEntity

class CategoryAdapter(
    private val onEdit: (CategoryEntity)->Unit,
    private val onDelete: (CategoryEntity)->Unit
) : ListAdapter<CategoryEntity, CategoryAdapter.VH>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return VH(v, onEdit, onDelete)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        itemView: View,
        val onEdit: (CategoryEntity)->Unit,
        val onDelete: (CategoryEntity)->Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvCategoryName)
        private val btnEdit = itemView.findViewById<ImageButton>(R.id.btnEdit)
        private val btnDelete = itemView.findViewById<ImageButton>(R.id.btnDelete)

        fun bind(cat: CategoryEntity) {
            tvName.text = cat.name
            btnEdit.setOnClickListener { onEdit(cat) }
            btnDelete.setOnClickListener { onDelete(cat) }
        }
    }

    class Diff : DiffUtil.ItemCallback<CategoryEntity>() {
        override fun areItemsTheSame(a: CategoryEntity, b: CategoryEntity) = a.id == b.id
        override fun areContentsTheSame(a: CategoryEntity, b: CategoryEntity) = a == b
    }
}
