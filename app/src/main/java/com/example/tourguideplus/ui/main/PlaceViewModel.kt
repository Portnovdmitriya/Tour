package com.example.tourguideplus.ui.main

import androidx.lifecycle.*
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceEntity
import kotlinx.coroutines.launch

class PlaceViewModel(application: TourGuideApp) : AndroidViewModel(application) {

    private val repository = application.placeRepository

    // LiveData со списком мест
    val places: LiveData<List<PlaceEntity>> = repository.allPlaces

    // Для выбранного места
    private val _selectedPlace = MutableLiveData<PlaceEntity?>()
    val selectedPlace: LiveData<PlaceEntity?> = _selectedPlace

    fun selectPlace(place: PlaceEntity) {
        _selectedPlace.value = place
    }

    // Добавление нового места
    fun addPlace(place: PlaceEntity) = viewModelScope.launch {
        repository.insert(place)
    }

    // Обновление существующего
    fun updatePlace(place: PlaceEntity) = viewModelScope.launch {
        repository.update(place)
    }

    // Удаление места
    fun deletePlace(place: PlaceEntity) = viewModelScope.launch {
        repository.delete(place)
    }
}

class PlaceViewModelFactory(
    private val application: TourGuideApp
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PlaceViewModel::class.java) ->
                PlaceViewModel(application) as T
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

