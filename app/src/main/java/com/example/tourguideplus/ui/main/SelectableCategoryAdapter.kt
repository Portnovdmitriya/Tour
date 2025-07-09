package com.example.tourguideplus.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tourguideplus.R
import com.example.tourguideplus.data.model.CategoryEntity

class SelectableCategoryAdapter(
    private val categories: List<CategoryEntity>
) : RecyclerView.Adapter<SelectableCategoryAdapter.VH>() {

    private val selected = BooleanArray(categories.size)

    /** Вернёт список выбранных ID */
    fun getSelectedIds(): List<Long> =
        categories.mapIndexedNotNull { idx, cat -> if (selected[idx]) cat.id else null }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selectable_category, parent, false)
        return VH(v)
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(categories[position], selected[position]) { checked ->
            selected[position] = checked
        }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cb: CheckBox = itemView.findViewById(R.id.cb_category)
        private val name: TextView = itemView.findViewById(R.id.tv_category_name)

        fun bind(cat: CategoryEntity, isChecked: Boolean, onCheck: (Boolean)->Unit) {
            name.text = cat.name
            cb.isChecked = isChecked
            cb.setOnCheckedChangeListener { _, checked ->
                onCheck(checked)
            }
        }
    }
}
