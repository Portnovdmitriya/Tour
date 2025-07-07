package com.example.tourguideplus.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tourguideplus.R
import com.example.tourguideplus.data.model.PlaceEntity

class PlaceAdapter(
    private val onItemClick: (PlaceEntity) -> Unit
) : ListAdapter<PlaceEntity, PlaceAdapter.PlaceViewHolder>(PlaceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlaceViewHolder(
        itemView: View,
        private val onItemClick: (PlaceEntity) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.tv_place_name)
        private val categoryText: TextView = itemView.findViewById(R.id.tv_place_category)

        fun bind(place: PlaceEntity) {
            nameText.text = place.name
            categoryText.text = place.category
            itemView.setOnClickListener { onItemClick(place) }
        }
    }

    class PlaceDiffCallback : DiffUtil.ItemCallback<PlaceEntity>() {
        override fun areItemsTheSame(old: PlaceEntity, new: PlaceEntity) =
            old.id == new.id

        override fun areContentsTheSame(old: PlaceEntity, new: PlaceEntity) =
            old == new
    }
}
