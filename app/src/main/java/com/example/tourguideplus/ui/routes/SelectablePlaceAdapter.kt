package com.example.tourguideplus.ui.routes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tourguideplus.R
import com.example.tourguideplus.data.model.PlaceEntity

class SelectablePlaceAdapter(
    private val places: List<PlaceEntity>
) : RecyclerView.Adapter<SelectablePlaceAdapter.VH>() {

    // Состояние чекбоксов
    private val selected = BooleanArray(places.size) { false }

    fun getSelectedPlaceIds(): List<Long> =
        places.mapIndexedNotNull { idx, place -> if (selected[idx]) place.id else null }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selectable_place, parent, false)
        return VH(v)
    }

    override fun getItemCount() = places.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(places[position], selected[position]) {
            selected[position] = it
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTv: TextView = itemView.findViewById(R.id.tv_place_name)
        private val check: CheckBox = itemView.findViewById(R.id.cb_select)
        fun bind(place: PlaceEntity, isChecked: Boolean, onCheck: (Boolean)->Unit) {
            nameTv.text = place.name
            check.isChecked = isChecked
            check.setOnCheckedChangeListener { _, checked ->
                onCheck(checked)
            }
        }
    }
}
