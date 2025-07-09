package com.example.tourguideplus.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.data.model.PlaceWithCategories

@Dao
interface PlaceDao {

    /** Список всех мест */
    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getAllPlaces(): LiveData<List<PlaceEntity>>

    /** Получить место по ID */
    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getPlaceById(id: Long): PlaceEntity?

    /** Вставить новое место, возвращая его ID */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long

    /** Обновить существующее */
    @Update
    suspend fun updatePlace(place: PlaceEntity)

    /** Удалить */
    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    @Transaction
    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getPlacesWithCategories(): LiveData<List<PlaceWithCategories>>
}
