package com.example.tourguideplus.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tourguideplus.data.model.WeatherCacheEntity

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE city = :city")
    suspend fun getCache(city: String): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cache: WeatherCacheEntity)

    @Query("DELETE FROM weather_cache WHERE timestamp < :threshold")
    suspend fun clearOld(threshold: Long)
}
