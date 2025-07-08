package com.example.tourguideplus.data.repository

import androidx.lifecycle.LiveData
import com.example.tourguideplus.data.dao.RouteDao
import com.example.tourguideplus.data.model.RouteEntity

class RouteRepository(private val dao: RouteDao) {
    val allRoutes: LiveData<List<RouteEntity>> = dao.getAllRoutes()

    suspend fun getById(id: Long): RouteEntity? = dao.getRouteById(id)
    suspend fun insert(route: RouteEntity): Long = dao.insertRoute(route)
    suspend fun update(route: RouteEntity) = dao.updateRoute(route)
    suspend fun delete(route: RouteEntity) = dao.deleteRoute(route)
}
