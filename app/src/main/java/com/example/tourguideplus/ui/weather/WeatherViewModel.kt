package com.example.tourguideplus.ui.weather

import android.app.Application
import androidx.lifecycle.*
import com.example.tourguideplus.R
import com.example.tourguideplus.data.weather.WeatherRepository
import com.example.tourguideplus.data.weather.WeatherResponse
import kotlinx.coroutines.launch

class WeatherViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = WeatherRepository()
    private val _weather = MutableLiveData<WeatherState>()
    val weather: LiveData<WeatherState> = _weather

    fun load(lat: Double, lon: Double) = viewModelScope.launch {
        _weather.value = WeatherState.Loading
        try {
            val key = getApplication<Application>().getString(R.string.owm_api_key)
            val resp = repo.getCurrent(lat, lon, key)
            _weather.value = WeatherState.Success(resp)
        } catch (e: Exception) {
            _weather.value = WeatherState.Error(e.message ?: "Ошибка сети")
        }
    }
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}
