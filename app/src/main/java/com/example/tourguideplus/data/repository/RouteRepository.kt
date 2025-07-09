package com.example.tourguideplus.data.repository

import androidx.lifecycle.LiveData
import com.example.tourguideplus.data.dao.RouteDao
import com.example.tourguideplus.data.model.RouteEntity
import com.example.tourguideplus.data.model.RoutePlaceCrossRef
import com.example.tourguideplus.data.model.RouteWithPlaces

class RouteRepository(private val dao: RouteDao) {

    val allRoutesWithPlaces: LiveData<List<RouteWithPlaces>> =
        dao.getAllRoutesWithPlaces()

    suspend fun getRouteWithPlaces(id: Long) =
        dao.getRouteWithPlacesById(id)

    // Используем новый метод DAO
    suspend fun createRouteWithPlaces(route: RouteEntity, placeIds: List<Long>): Long {
        return dao.insertRouteWithPlaces(route, placeIds)
    }

    // И аналогично для обновления
    suspend fun updateRouteWithPlaces(
        route: RouteEntity,
        placeIds: List<Long>
    ) = dao.updateRouteWithPlaces(route, placeIds)

    suspend fun deleteRoute(route: RouteEntity) {
        dao.deleteRoute(route)
        dao.deleteCrossRefsForRoute(route.id)
    }
}
