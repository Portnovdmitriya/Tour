package com.example.tourguideplus.data.repository

import com.example.tourguideplus.data.dao.WeatherCacheDao
import com.example.tourguideplus.data.model.WeatherCacheEntity

class WeatherCacheRepository(private val dao: WeatherCacheDao) {

    /** Получить кэш по городу (или null, если нет) */
    suspend fun getCache(city: String): WeatherCacheEntity? =
        dao.getCache(city)

    /** Вставить или обновить запись кэша */
    suspend fun upsert(cache: WeatherCacheEntity) =
        dao.upsert(cache)

    /** Очистить старый кэш до заданного времени */
    suspend fun clearOld(threshold: Long) =
        dao.clearOld(threshold)
}
