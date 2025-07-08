// app/src/main/java/com/example/tourguideplus/data/dao/RouteDao.kt
package com.example.tourguideplus.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tourguideplus.data.model.RouteEntity
import com.example.tourguideplus.data.model.RoutePlaceCrossRef
import com.example.tourguideplus.data.model.RouteWithPlaces

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes ORDER BY name ASC")
    fun getAllRoutes(): LiveData<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE id = :id")
    suspend fun getRouteById(id: Long): RouteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity): Long

    @Update
    suspend fun updateRoute(route: RouteEntity)

    @Delete
    suspend fun deleteRoute(route: RouteEntity)

    // Вставить связи маршрут–место
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: RoutePlaceCrossRef)

    // Удалить все связи для маршрута
    @Query("DELETE FROM route_place_crossref WHERE routeId = :routeId")
    suspend fun deleteCrossRefsForRoute(routeId: Long)

    // Получить все маршруты вместе с местами
    @Transaction
    @Query("SELECT * FROM routes ORDER BY name ASC")
    fun getAllRoutesWithPlaces(): LiveData<List<RouteWithPlaces>>

    // Получить один маршрут вместе с местами
    @Transaction
    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getRouteWithPlacesById(routeId: Long): RouteWithPlaces?
}
