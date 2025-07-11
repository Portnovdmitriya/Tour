package com.example.tourguideplus.ui.notes

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.NoteEntity
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.databinding.DialogAddEditNoteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddEditNoteDialogFragment(
    private val existing: Pair<NoteEntity, PlaceEntity>? = null
) : DialogFragment() {

    private lateinit var binding: DialogAddEditNoteBinding
    private lateinit var vm: NoteViewModel
    private lateinit var places: List<PlaceEntity>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddEditNoteBinding.inflate(layoutInflater)

        // Инициализируем VM
        val app = requireActivity().application as TourGuideApp
        vm = ViewModelProvider(this, NoteViewModelFactory(app))
            .get(NoteViewModel::class.java)

        // Загружаем список мест
        val placeRepo = app.placeRepository
        placeRepo.allPlaces.observe(this) { list ->
            places = list
            val names = places.map { it.name }
            binding.spinnerPlaces.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                names
            )
            // Если редактируем, выбрать позицию существующего места
            existing?.second?.let { place ->
                val pos = places.indexOfFirst { it.id == place.id }
                if (pos >= 0) binding.spinnerPlaces.setSelection(pos)
            }
        }

        // Заполняем поля при редактировании
        existing?.let { (note, place) ->
            binding.etNoteText.setText(note.text)
        }

        // Сохранить
        binding.btnSave.setOnClickListener {
            val pos = binding.spinnerPlaces.selectedItemPosition
            if (pos < 0 || pos >= places.size) return@setOnClickListener

            val placeId = places[pos].id
            val text = binding.etNoteText.text.toString().trim()
            if (text.isEmpty()) {
                binding.etNoteText.error = "Введите текст"
                return@setOnClickListener
            }

            if (existing == null) {
                vm.add(
                    NoteEntity(
                        placeId = placeId,
                        text = text
                    )
                )
            } else {
                val note = existing.first.copy(
                    placeId = placeId,
                    text = text
                )
                vm.update(note)
            }
            dismiss()
        }

        // Отмена
        binding.btnCancel.setOnClickListener { dismiss() }

        // Собираем и возвращаем
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }
}
