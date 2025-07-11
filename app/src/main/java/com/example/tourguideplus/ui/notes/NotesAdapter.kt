package com.example.tourguideplus.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tourguideplus.R
import com.example.tourguideplus.data.model.NoteWithPlace

class NotesAdapter(
    private val onClick: (NoteWithPlace) -> Unit
) : ListAdapter<NoteWithPlace, NotesAdapter.VH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPlace = itemView.findViewById<TextView>(R.id.tvPlaceName)
        private val tvText  = itemView.findViewById<TextView>(R.id.tvNoteText)

        fun bind(nwp: NoteWithPlace) {
            tvPlace.text = nwp.place.name
            tvText.text  = nwp.note.text
            itemView.setOnClickListener { onClick(nwp) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<NoteWithPlace>() {
        override fun areItemsTheSame(old: NoteWithPlace, new: NoteWithPlace) =
            old.note.id == new.note.id

        override fun areContentsTheSame(old: NoteWithPlace, new: NoteWithPlace) =
            old == new
    }
}