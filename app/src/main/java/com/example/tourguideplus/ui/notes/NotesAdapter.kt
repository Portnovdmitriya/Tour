package com.example.tourguideplus.ui.notes

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tourguideplus.R
import com.example.tourguideplus.data.model.NoteWithPlace

class NotesAdapter(
    private val onClick: (NoteWithPlace) -> Unit,
    private val onToggleDone: (NoteWithPlace, Boolean) -> Unit
) : ListAdapter<NoteWithPlace, NotesAdapter.VH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return VH(view, onClick, onToggleDone)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        itemView: View,
        private val onClick: (NoteWithPlace) -> Unit,
        private val onToggleDone: (NoteWithPlace, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val cbDone = itemView.findViewById<CheckBox>(R.id.cbDone)
        private val tvPlace = itemView.findViewById<TextView>(R.id.tvPlaceName)
        private val tvText  = itemView.findViewById<TextView>(R.id.tvNoteText)

        fun bind(nwp: NoteWithPlace) {
            // Текст
            tvPlace.text = nwp.place?.name ?: "Место удалено"
            tvText.text  = nwp.note.text

            // Чекбокс
            cbDone.setOnCheckedChangeListener(null)
            cbDone.isChecked = nwp.note.isDone
            cbDone.setOnCheckedChangeListener { _, checked ->
                onToggleDone(nwp, checked)
            }

            // Визуал завершённости
            applyStrike(tvPlace, nwp.note.isDone && nwp.place != null)
            applyStrike(tvText, nwp.note.isDone)

            itemView.setOnClickListener { onClick(nwp) }
        }

        private fun applyStrike(tv: TextView, strike: Boolean) {
            tv.paintFlags = if (strike)
                tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else
                tv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<NoteWithPlace>() {
        override fun areItemsTheSame(old: NoteWithPlace, new: NoteWithPlace) =
            old.note.id == new.note.id

        override fun areContentsTheSame(old: NoteWithPlace, new: NoteWithPlace) =
            old == new
    }
}
