package com.example.tourguideplus.data.weather

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("name") val city: String,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("main")    val main: Main,
    @SerializedName("wind")    val wind: Wind
)
data class Weather(
    @SerializedName("description") val description: String,
    @SerializedName("icon")        val icon: String
)
data class Main(
    @SerializedName("temp")      val temp: Double,
    @SerializedName("feels_like")val feelsLike: Double,
    @SerializedName("humidity")  val humidity: Int
)
data class Wind(
    @SerializedName("speed")     val speed: Double
)
