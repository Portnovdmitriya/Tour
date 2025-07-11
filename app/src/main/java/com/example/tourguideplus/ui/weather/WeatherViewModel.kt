package com.example.tourguideplus.ui.weather

import android.app.Application
import androidx.lifecycle.*
import com.example.tourguideplus.R
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.WeatherCacheEntity
import com.example.tourguideplus.data.weather.WeatherRepository
import com.example.tourguideplus.data.weather.WeatherResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = WeatherRepository()
    private val cacheRepo = (app as TourGuideApp).weatherCacheRepository
    private val gson = Gson()
    private val THRESHOLD = 30 * 60_000L   // 30 минут

    private val _weather = MutableLiveData<WeatherState>()
    val weather: LiveData<WeatherState> = _weather

    private val apiKey: String = app.getString(R.string.owm_api_key)

    fun loadByCity(city: String) = viewModelScope.launch(Dispatchers.IO) {
        _weather.postValue(WeatherState.Loading)

        // 1) Сначала попытка из кэша
        val now = System.currentTimeMillis()
        val cache = try { cacheRepo.getCache(city) } catch (_:Exception){ null }
        var emittedFromCache = false
        if (cache != null && now - cache.timestamp < THRESHOLD) {
            try {
                val cachedResp = gson.fromJson(cache.jsonData, WeatherResponse::class.java)
                _weather.postValue(WeatherState.Success(cachedResp))
                emittedFromCache = true
            } catch (_: Exception) { /* парсинг провалился */ }
        }

        // 2) Всегда пытаемся обновить из сети
        try {
            val fresh = repo.getCurrentByCity(city, apiKey)
            _weather.postValue(WeatherState.Success(fresh))
            // 3) Сохраняем в кэш
            val jsonStr = gson.toJson(fresh)
            cacheRepo.upsert(WeatherCacheEntity(city, jsonStr, now))
        } catch (e: Exception) {
            if (!emittedFromCache) {
                _weather.postValue(WeatherState.Error(e.message ?: "Ошибка сети"))
            }
        }
    }
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}