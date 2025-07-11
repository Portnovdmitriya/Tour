package com.example.tourguideplus.ui.main

import androidx.lifecycle.*
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.data.model.PlaceWithCategories
import kotlinx.coroutines.launch

class PlaceViewModel(application: TourGuideApp) : AndroidViewModel(application) {

    private val repo    = application.placeRepository
    private val catRepo = application.categoryRepository
    private val favRepo = application.favoriteRepository

    /** Все места с категориями */
    val placesWithCategories: LiveData<List<PlaceWithCategories>> =
        repo.allPlacesWithCategories

    /** Все места (простым списком) */
    val places: LiveData<List<PlaceEntity>> =
        repo.allPlaces

    /** Набор ID мест, отмеченных как избранное */
    val favoriteIds: LiveData<Set<Long>> =
        favRepo.allFavorites
            .map { list -> list.map { it.placeId }.toSet() }


    val favoritePlacesWithCategories: LiveData<List<PlaceWithCategories>> =
        MediatorLiveData<List<PlaceWithCategories>>().apply {
            var allPlaces: List<PlaceWithCategories> = emptyList()
            var favSet: Set<Long> = emptySet()

            fun update() {
                value = allPlaces.filter { it.place.id in favSet }
            }

            addSource(placesWithCategories) { list ->
                allPlaces = list
                update()
            }
            addSource(favoriteIds) { set ->
                favSet = set
                update()
            }
        }



    private val _selectedPlace = MutableLiveData<PlaceEntity?>()
    val selectedPlace: LiveData<PlaceEntity?> = _selectedPlace

    fun loadPlaceById(id: Long) = viewModelScope.launch {
        _selectedPlace.postValue(repo.getPlaceById(id))
    }



    fun createPlaceWithCategories(
        place: PlaceEntity,
        categoryIds: List<Long>
    ) = viewModelScope.launch {
        val newId = repo.insertReturnId(place)
        catRepo.assignCategories(newId, categoryIds)
    }

    fun updatePlaceWithCategories(
        place: PlaceEntity,
        categoryIds: List<Long>
    ) = viewModelScope.launch {
        repo.update(place)
        catRepo.assignCategories(place.id, categoryIds)
    }

    fun deletePlace(place: PlaceEntity) = viewModelScope.launch {
        repo.delete(place)
    }


    private val _wikiExtract = MutableLiveData<String?>()
    val wikiExtract: LiveData<String?> = _wikiExtract

    fun loadWikiSummary(title: String) = viewModelScope.launch {
        _wikiExtract.postValue(repo.fetchWikiSummary(title))
    }



    /** Переключить статус «избранного» для данного placeId */
    fun toggleFavorite(placeId: Long) = viewModelScope.launch {
        val current = favoriteIds.value ?: emptySet()
        if (placeId in current) favRepo.removeFavorite(placeId)
        else favRepo.addFavorite(placeId)
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
