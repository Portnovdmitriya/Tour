// app/src/main/java/com/example/tourguideplus/data/dao/RouteDao.kt
package com.example.tourguideplus.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tourguideplus.data.model.RouteEntity

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
}
