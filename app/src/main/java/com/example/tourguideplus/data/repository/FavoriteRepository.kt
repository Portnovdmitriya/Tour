package com.example.tourguideplus.data.repository

import androidx.lifecycle.LiveData
import com.example.tourguideplus.data.dao.FavoriteDao
import com.example.tourguideplus.data.model.FavoriteEntity

class FavoriteRepository(private val dao: FavoriteDao) {

    /** Все избранные места */
    val allFavorites: LiveData<List<FavoriteEntity>> = dao.getAllFavorites()

    /** Добавить в избранное */
    suspend fun addFavorite(placeId: Long) {
        dao.add(FavoriteEntity(placeId))
    }

    /** Убрать из избранного */
    suspend fun removeFavorite(placeId: Long) {
        dao.remove(FavoriteEntity(placeId))
    }

    /** Проверка, в избранном ли место */
    fun isFavorite(placeId: Long): LiveData<List<FavoriteEntity>> =
        // Можно вернуть LiveData и смотреть непустоту списка
        dao.getAllFavorites()
}
