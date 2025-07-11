package com.example.tourguideplus.ui.notes

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.NoteEntity
import com.example.tourguideplus.data.model.NoteWithPlace
import kotlinx.coroutines.launch

class NoteViewModel(application: TourGuideApp) : AndroidViewModel(application) {
    private val repo = application.noteRepository

    /** Список всех заметок c местами */
    val notesWithPlace: LiveData<List<NoteWithPlace>> = repo.allNotesWithPlace

    fun add(note: NoteEntity) = viewModelScope.launch {
        repo.insert(note)
    }
    fun update(note: NoteEntity) = viewModelScope.launch {
        repo.update(note)
    }
    fun delete(note: NoteEntity) = viewModelScope.launch {
        repo.delete(note)
    }
}

class NoteViewModelFactory(
    private val app: TourGuideApp
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}
