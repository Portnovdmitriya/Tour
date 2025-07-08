package com.example.tourguideplus.ui.routes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tourguideplus.R
import com.example.tourguideplus.data.model.RouteEntity

class RouteAdapter(
    private val onItemClick: (RouteEntity) -> Unit
) : ListAdapter<RouteEntity, RouteAdapter.RouteVH>(RouteDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteVH(v, onItemClick)
    }

    override fun onBindViewHolder(holder: RouteVH, position: Int) {
        holder.bind(getItem(position))
    }

    class RouteVH(itemView: View, val onItemClick: (RouteEntity)->Unit)
        : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.tv_route_name)
        private val desc: TextView = itemView.findViewById(R.id.tv_route_desc)
        fun bind(route: RouteEntity) {
            name.text = route.name
            desc.text = route.description
            itemView.setOnClickListener { onItemClick(route) }
        }
    }

    class RouteDiff : DiffUtil.ItemCallback<RouteEntity>() {
        override fun areItemsTheSame(old: RouteEntity, new: RouteEntity) =
            old.id == new.id
        override fun areContentsTheSame(old: RouteEntity, new: RouteEntity) =
            old == new
    }
}
