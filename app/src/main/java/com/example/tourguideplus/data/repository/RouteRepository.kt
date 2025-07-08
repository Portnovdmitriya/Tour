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

    // Новый метод: создаём маршрут и связи
    suspend fun createRouteWithPlaces(
        route: RouteEntity,
        placeIds: List<Long>
    ): Long {

        val newId = dao.insertRoute(route)

        dao.deleteCrossRefsForRoute(newId) // очистка на случай обновления
        placeIds.forEach { pid ->
            dao.insertCrossRef(RoutePlaceCrossRef(newId, pid))
        }
        return newId
    }

    suspend fun updateRouteWithPlaces(
        route: RouteEntity,
        placeIds: List<Long>
    ) {
        dao.updateRoute(route)
        dao.deleteCrossRefsForRoute(route.id)
        placeIds.forEach { pid ->
            dao.insertCrossRef(RoutePlaceCrossRef(route.id, pid))
        }
    }

    suspend fun deleteRoute(route: RouteEntity) {
        dao.deleteRoute(route)
        dao.deleteCrossRefsForRoute(route.id)
    }
}
