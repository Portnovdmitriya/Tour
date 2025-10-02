package com.example.tourguideplus.share

import android.util.Base64
import com.example.tourguideplus.data.model.RouteWithPlaces
import com.example.tourguideplus.data.model.PlaceEntity
import com.google.gson.Gson
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/** DTO для обмена (минимально нужное) */
data class SharedPlace(
    val name: String,
    val description: String?,
    val latitude: Double?,
    val longitude: Double?,
    val photoUri: String?
)
data class SharedRoute(
    val version: Int = 1,
    val name: String,
    val description: String?,
    val places: List<SharedPlace>
)

object RouteShareCodec {
    private val gson = Gson()

    /** В payload: gzip(json)->base64(URL_SAFE, NO_WRAP) */
    fun encode(rwp: RouteWithPlaces): String {
        val dto = SharedRoute(
            name = rwp.route.name,
            description = rwp.route.description,
            places = rwp.places.map { p ->
                SharedPlace(
                    name = p.name,
                    description = p.description,
                    latitude = p.latitude,
                    longitude = p.longitude,
                    photoUri = p.photoUri
                )
            }
        )
        val json = gson.toJson(dto).toByteArray(Charsets.UTF_8)
        val gz = ByteArrayOutputStream().use { out ->
            GZIPOutputStream(out).use { it.write(json) }
            out.toByteArray()
        }
        return Base64.encodeToString(gz, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    fun decode(payload: String): SharedRoute? = try {
        val gz = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
        val json = GZIPInputStream(ByteArrayInputStream(gz)).use { it.readBytes() }
        gson.fromJson(json.toString(Charsets.UTF_8), SharedRoute::class.java)
    } catch (_: Exception) { null }
}

/** Утилита преобразования в PlaceEntity (без категорий) */
fun SharedPlace.toPlaceEntity(): PlaceEntity = PlaceEntity(
    id = 0L,
    name = name,
    description = description ?: "",
    category = "", // категории опустим в MVP
    latitude = latitude,
    longitude = longitude,
    photoUri = photoUri
)
