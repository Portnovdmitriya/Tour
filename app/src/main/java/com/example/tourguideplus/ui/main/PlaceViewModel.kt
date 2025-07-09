package com.example.tourguideplus.ui.main

import androidx.lifecycle.*
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceEntity
import kotlinx.coroutines.launch

class PlaceViewModel(application: TourGuideApp) : AndroidViewModel(application) {

    private val repo = application.placeRepository
    private val catRepo = application.categoryRepository

    // Список всех мест
    val places: LiveData<List<PlaceEntity>> = repo.allPlaces

    // Для выбранного места
    private val _selectedPlace = MutableLiveData<PlaceEntity?>()
    val selectedPlace: LiveData<PlaceEntity?> = _selectedPlace

    fun loadPlaceById(id: Long) = viewModelScope.launch {
        _selectedPlace.postValue(repo.getPlaceById(id))
    }

    fun selectPlace(place: PlaceEntity) {
        _selectedPlace.value = place
    }

    // Экран «Справка» (вики)
    private val _wikiExtract = MutableLiveData<String?>()
    val wikiExtract: LiveData<String?> = _wikiExtract
    fun loadWikiSummary(title: String) = viewModelScope.launch {
        _wikiExtract.postValue(repo.fetchWikiSummary(title))
    }

    // Стандартное удаление
    fun deletePlace(place: PlaceEntity) = viewModelScope.launch {
        repo.delete(place)
    }
    // классический update без категорий
    fun updatePlace(place: PlaceEntity) = viewModelScope.launch {
        repo.update(place)
    }

    fun createPlaceWithCategories(
        place: PlaceEntity,
        categoryIds: List<Long>
    ) = viewModelScope.launch {

        val newId: Long = repo.insertReturnId(place)

        catRepo.assignCategories(newId, categoryIds)
    }


    fun updatePlaceWithCategories(
        place: PlaceEntity,
        categoryIds: List<Long>
    ) = viewModelScope.launch {

        repo.update(place)

        catRepo.assignCategories(place.id, categoryIds)
    }
}

class PlaceViewModelFactory(
    private val application: TourGuideApp
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(PlaceViewModel::class.java)) {
            PlaceViewModel(application) as T
        } else throw IllegalArgumentException("Unknown VM class: $modelClass")
}
