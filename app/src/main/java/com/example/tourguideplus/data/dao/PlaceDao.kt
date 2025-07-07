package com.example.tourguideplus.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tourguideplus.data.model.PlaceEntity

@Dao
interface PlaceDao {

    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getAllPlaces(): LiveData<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getPlaceById(id: Long): PlaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long

    @Update
    suspend fun updatePlace(place: PlaceEntity)

    @Delete
    suspend fun deletePlace(place: PlaceEntity)
}
