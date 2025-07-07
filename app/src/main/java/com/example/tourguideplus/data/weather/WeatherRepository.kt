package com.example.tourguideplus.data.weather

class WeatherRepository {
    private val api = WeatherService.create()

    suspend fun getCurrentByCity(city: String, key: String): WeatherResponse =
        api.getCurrentWeatherByCity(city, apiKey = key)

    // Если понадобится геолокация
    suspend fun getCurrent(lat: Double, lon: Double, key: String): WeatherResponse =
        api.getCurrentWeather(lat, lon, apiKey = key)
}

