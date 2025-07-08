// app/src/main/java/com/example/tourguideplus/data/dao/RouteDao.kt
package com.example.tourguideplus.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tourguideplus.data.model.RouteEntity
import com.example.tourguideplus.data.model.RoutePlaceCrossRef
import com.example.tourguideplus.data.model.RouteWithPlaces

// app/src/main/java/com/example/tourguideplus/data/dao/RouteDao.kt
@Dao
interface RouteDao {
    // Существующие методы…

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity): Long

    @Update
    suspend fun updateRoute(route: RouteEntity)

    @Delete
    suspend fun deleteRoute(route: RouteEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: RoutePlaceCrossRef)

    @Query("DELETE FROM route_place_crossref WHERE routeId = :routeId")
    suspend fun deleteCrossRefsForRoute(routeId: Long)

    @Transaction
    @Query("SELECT * FROM routes ORDER BY name ASC")
    fun getAllRoutesWithPlaces(): LiveData<List<RouteWithPlaces>>

    @Transaction
    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getRouteWithPlacesById(routeId: Long): RouteWithPlaces?

    // ← Вот этот метод
    @Transaction
    suspend fun insertRouteWithPlaces(
        route: RouteEntity,
        placeIds: List<Long>
    ): Long {
        // 1) Вставляем (или обновляем) маршрут
        val id = insertRoute(route)
        // 2) Сначала чистим старые связи (для update), но для нового id их нет
        deleteCrossRefsForRoute(id)
        // 3) Вставляем все кросс-записи
        placeIds.forEach { pid ->
            insertCrossRef(RoutePlaceCrossRef(id, pid))
        }
        return id
    }

    @Transaction
    suspend fun updateRouteWithPlaces(
        route: RouteEntity,
        placeIds: List<Long>
    ) {
        updateRoute(route)
        deleteCrossRefsForRoute(route.id)
        placeIds.forEach { pid ->
            insertCrossRef(RoutePlaceCrossRef(route.id, pid))
        }
    }
}
