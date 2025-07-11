package com.example.tourguideplus.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.tourguideplus.data.model.NoteEntity
import com.example.tourguideplus.data.model.NoteWithPlace

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE placeId = :placeId")
    fun getNotesForPlace(placeId: Long): LiveData<List<NoteEntity>>

    @Insert
    suspend fun insert(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Transaction
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotesWithPlace(): LiveData<List<NoteWithPlace>>
}
