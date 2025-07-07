package com.example.tourguideplus.data.repository

import androidx.lifecycle.LiveData
import com.example.tourguideplus.data.AppDatabase
import com.example.tourguideplus.data.dao.PlaceDao
import com.example.tourguideplus.data.model.PlaceEntity

class PlaceRepository(private val placeDao: PlaceDao) {

    val allPlaces: LiveData<List<PlaceEntity>> = placeDao.getAllPlaces()

    suspend fun getPlaceById(id: Long): PlaceEntity? =
        placeDao.getPlaceById(id)

    suspend fun insert(place: PlaceEntity): Long =
        placeDao.insertPlace(place)

    suspend fun update(place: PlaceEntity) =
        placeDao.updatePlace(place)

    suspend fun delete(place: PlaceEntity) =
        placeDao.deletePlace(place)
}
