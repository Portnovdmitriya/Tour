package com.example.tourguideplus.data.weather

class WeatherRepository {
    private val api = WeatherService.create()

    suspend fun getCurrentByCity(city: String, key: String): WeatherResponse =
        api.getCurrentWeatherByCity(city, apiKey = key)

}

