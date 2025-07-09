package com.example.tourguideplus.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tourguideplus.data.model.CategoryEntity
import com.example.tourguideplus.data.model.PlaceCategoryCrossRef
import com.example.tourguideplus.data.model.PlaceWithCategories

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(cat: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(ref: PlaceCategoryCrossRef)

    @Query("DELETE FROM place_category_crossref WHERE placeId = :placeId")
    suspend fun deleteCrossRefsForPlace(placeId: Long)

    @Transaction
    @Query("SELECT * FROM places WHERE id = :placeId")
    suspend fun getPlaceWithCategories(placeId: Long): PlaceWithCategories?
}

