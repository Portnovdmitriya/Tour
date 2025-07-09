package com.example.tourguideplus.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tourguideplus.R
import com.example.tourguideplus.data.model.CategoryEntity

class SelectableCategoryAdapter(
    private val onToggle: (CategoryEntity, Boolean) -> Unit
) : ListAdapter<CategoryEntity, SelectableCategoryAdapter.VH>(DiffCallback()) {

    private val selectedIds = mutableSetOf<Long>()

    /** Задаёт изначально отмеченные категории */
    fun setSelectedIds(ids: List<Long>) {
        selectedIds.clear()
        selectedIds.addAll(ids)
        notifyDataSetChanged()
    }

    /** Возвращает все отмеченные id */
    fun getSelectedIds(): List<Long> = selectedIds.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selectable_category, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cat = getItem(position)
        holder.bind(cat, selectedIds.contains(cat.id))
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cb = itemView.findViewById<CheckBox>(R.id.cb_category)
        fun bind(cat: CategoryEntity, isChecked: Boolean) {
            cb.text = cat.name
            cb.setOnCheckedChangeListener(null)
            cb.isChecked = isChecked
            cb.setOnCheckedChangeListener { _, checked ->
                if (checked) selectedIds.add(cat.id)
                else selectedIds.remove(cat.id)
                onToggle(cat, checked)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryEntity>() {
        override fun areItemsTheSame(a: CategoryEntity, b: CategoryEntity) =
            a.id == b.id

        override fun areContentsTheSame(a: CategoryEntity, b: CategoryEntity) =
            a == b
    }
}
