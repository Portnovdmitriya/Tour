package com.example.tourguideplus.data.repository

import androidx.lifecycle.LiveData
import com.example.tourguideplus.data.dao.PlaceDao
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.data.model.PlaceWithCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class PlaceRepository(private val dao: PlaceDao) {

    // SQL-часть
    val allPlaces: LiveData<List<PlaceEntity>> = dao.getAllPlaces()

    suspend fun getPlaceById(id: Long): PlaceEntity? =
        dao.getPlaceById(id)

    suspend fun insertReturnId(place: PlaceEntity): Long =
        dao.insertPlace(place)

    suspend fun update(place: PlaceEntity) =
        dao.updatePlace(place)

    suspend fun delete(place: PlaceEntity) =
        dao.deletePlace(place)

    val allPlacesWithCategories: LiveData<List<PlaceWithCategories>> =
        dao.getPlacesWithCategories()

    suspend fun fetchWikiSummary(title: String): String? = withContext(Dispatchers.IO) {
        try {
            // Формируем URL
            val encoded = URLEncoder.encode(title, "UTF-8")
            val url = URL("https://ru.wikipedia.org/api/rest_v1/page/summary/$encoded")

            // Открываем соединение и читаем ответ
            (url.openConnection() as HttpURLConnection).run {
                requestMethod = "GET"
                connectTimeout = 5_000
                readTimeout    = 5_000
                inputStream.bufferedReader().use { buf ->
                    val json = buf.readText()
                    // Парсим JSON и возвращаем поле "extract"
                    JSONObject(json).optString("extract", null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
