package com.example.tourguideplus.data.repository

import androidx.lifecycle.LiveData
import com.example.tourguideplus.data.dao.CategoryDao
import com.example.tourguideplus.data.model.CategoryEntity
import com.example.tourguideplus.data.model.PlaceCategoryCrossRef
import com.example.tourguideplus.data.model.PlaceWithCategories

class CategoryRepository(private val dao: CategoryDao) {
    val all: LiveData<List<CategoryEntity>> = dao.getAllCategories()

    suspend fun add(name: String): Long =
        dao.insertCategory(CategoryEntity(name = name))

    suspend fun assignCategories(placeId: Long, categoryIds: List<Long>) {
        dao.deleteCrossRefsForPlace(placeId)
        categoryIds.forEach { cid ->
            dao.insertCrossRef(PlaceCategoryCrossRef(placeId, cid))
        }
    }

    suspend fun getPlaceWithCategories(placeId: Long): PlaceWithCategories? =
        dao.getPlaceWithCategories(placeId)

    suspend fun update(cat: CategoryEntity) {
        dao.updateCategory(cat)
    }

    suspend fun delete(cat: CategoryEntity) {
        dao.deleteCategory(cat)
    }
}