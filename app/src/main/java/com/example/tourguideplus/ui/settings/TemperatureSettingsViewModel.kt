package com.example.tourguideplus.ui.settings

import androidx.lifecycle.*
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.SettingEntity
import kotlinx.coroutines.launch

class TemperatureSettingsViewModel(app: TourGuideApp) : AndroidViewModel(app) {
    private val repo = app.settingRepository
    private val KEY = "temperature_unit"

    /** Сейчас в БД хранится "C" или "F"; по умолчанию "C" */
    val unit: LiveData<String> = repo.getSetting(KEY)
        .map { it?.value ?: "C" }

    /** Сохранить новую единицу */
    fun setUnit(u: String) = viewModelScope.launch {
        repo.upsert(SettingEntity(KEY, u))
    }
}

class TemperatureSettingsViewModelFactory(
    private val app: TourGuideApp
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(c: Class<T>): T {
        if (c.isAssignableFrom(TemperatureSettingsViewModel::class.java))
            return TemperatureSettingsViewModel(app) as T
        throw IllegalArgumentException()
    }
}
