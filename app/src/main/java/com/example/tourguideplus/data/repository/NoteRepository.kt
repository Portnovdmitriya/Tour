package com.example.tourguideplus.data.repository

import androidx.lifecycle.LiveData
import com.example.tourguideplus.data.dao.NoteDao
import com.example.tourguideplus.data.model.NoteEntity

class NoteRepository(private val dao: NoteDao) {

    /** Все заметки для данного места */
    fun getNotesForPlace(placeId: Long): LiveData<List<NoteEntity>> =
        dao.getNotesForPlace(placeId)

    /** Добавить новую заметку */
    suspend fun insert(note: NoteEntity) {
        dao.insert(note)
    }

    /** Обновить существующую */
    suspend fun update(note: NoteEntity) {
        dao.update(note)
    }

    /** Удалить заметку */
    suspend fun delete(note: NoteEntity) {
        dao.delete(note)
    }
}
