package com.example.tourguideplus.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.NoteWithPlace
import com.example.tourguideplus.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: NoteViewModel
    private lateinit var adapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем ViewModel
        val app = requireActivity().application as TourGuideApp
        vm = ViewModelProvider(this, NoteViewModelFactory(app))
            .get(NoteViewModel::class.java)

        // Настраиваем адаптер и RecyclerView
        adapter = NotesAdapter { nwp: NoteWithPlace ->
            // Открываем диалог редактирования
            AddEditNoteDialogFragment(nwp.note to nwp.place)
                .show(parentFragmentManager, "EditNote")
        }
        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NotesFragment.adapter
        }

        // Наблюдаем за списком заметок
        vm.notesWithPlace.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // Обработчик добавления новой заметки
        binding.fabAddNote.setOnClickListener {
            AddEditNoteDialogFragment()
                .show(parentFragmentManager, "AddNote")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
