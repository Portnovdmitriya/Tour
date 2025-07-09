package com.example.tourguideplus.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tourguideplus.R
import com.example.tourguideplus.data.model.PlaceWithCategories

class PlaceWithCategoriesAdapter(
    private val onItemClick: (PlaceWithCategories) -> Unit
) : ListAdapter<PlaceWithCategories, PlaceWithCategoriesAdapter.VH>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return VH(v, onItemClick)
    }

    override fun onBindViewHolder(holder: VH, pos: Int) {
        holder.bind(getItem(pos))
    }

    class VH(itemView: View, val onItemClick: (PlaceWithCategories) -> Unit)
        : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tv_place_name)
        private val tvCats = itemView.findViewById<TextView>(R.id.tv_place_category)

        fun bind(pwc: PlaceWithCategories) {
            tvName.text = pwc.place.name
            tvCats.text = pwc.categories.joinToString(", ") { it.name }
            itemView.setOnClickListener { onItemClick(pwc) }
        }
    }

    class Diff : DiffUtil.ItemCallback<PlaceWithCategories>() {
        override fun areItemsTheSame(a: PlaceWithCategories, b: PlaceWithCategories) =
            a.place.id == b.place.id
        override fun areContentsTheSame(a: PlaceWithCategories, b: PlaceWithCategories) =
            a == b
    }
}
