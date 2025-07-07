package com.example.tourguideplus.data.weather

class WeatherRepository {
    private val api = WeatherService.create()
    suspend fun getCurrent(lat: Double, lon: Double, key: String): WeatherResponse =
        api.getCurrentWeather(lat, lon, apiKey = key)
}
