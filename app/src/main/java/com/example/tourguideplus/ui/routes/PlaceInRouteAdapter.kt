package com.example.tourguideplus.ui.routes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tourguideplus.R
import com.example.tourguideplus.data.model.PlaceEntity

class PlaceInRouteAdapter(
    private var items: List<PlaceEntity>,
    private val onRemove: (PlaceEntity) -> Unit
) : RecyclerView.Adapter<PlaceInRouteAdapter.VH>() {

    fun update(newItems: List<PlaceEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place_in_route, parent, false),
        onRemove
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    class VH(itemView: View, val onRemove: (PlaceEntity)->Unit)
        : RecyclerView.ViewHolder(itemView) {
        private val nameTv: TextView = itemView.findViewById(R.id.tv_place_name)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btn_remove_place)
        fun bind(place: PlaceEntity) {
            nameTv.text = place.name
            btnRemove.setOnClickListener { onRemove(place) }
        }
    }
}
