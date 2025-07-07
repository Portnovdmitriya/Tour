package com.example.tourguideplus.data.wiki

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Модель ответа
data class WikiSummary(
    val extract: String
)

interface WikiService {
    @GET("page/summary/{title}")
    suspend fun getSummary(
        @Path("title") title: String
    ): WikiSummary

    companion object {
        private const val BASE = "https://ru.wikipedia.org/api/rest_v1/"
        fun create(): WikiService = Retrofit.Builder()
            .baseUrl(BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WikiService::class.java)
    }
}
