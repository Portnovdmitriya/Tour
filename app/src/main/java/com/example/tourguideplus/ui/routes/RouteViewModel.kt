// app/src/main/java/com/example/tourguideplus/ui/routes/RouteViewModel.kt
package com.example.tourguideplus.ui.routes

import androidx.lifecycle.*
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.RouteEntity
import com.example.tourguideplus.data.model.RouteWithPlaces
import kotlinx.coroutines.launch

class RouteViewModel(app: TourGuideApp) : AndroidViewModel(app) {
    private val repo = app.routeRepository

    // Список маршрутов с местами
    val routesWithPlaces: LiveData<List<RouteWithPlaces>> = repo.allRoutesWithPlaces

    private val _selected = MutableLiveData<RouteWithPlaces?>()
    val selected: LiveData<RouteWithPlaces?> = _selected

    fun select(routeId: Long) = viewModelScope.launch {
        _selected.value = repo.getRouteWithPlaces(routeId)
    }

    // Создать новый маршрут вместе с местами
    fun createRoute(name: String, desc: String?, placeIds: List<Long>) = viewModelScope.launch {
        val route = RouteEntity(name = name, description = desc)
        repo.createRouteWithPlaces(route, placeIds)
    }

    // Обновить существующий
    fun updateRoute(route: RouteEntity, placeIds: List<Long>) = viewModelScope.launch {
        repo.updateRouteWithPlaces(route, placeIds)
    }

    fun deleteRoute(route: RouteEntity) = viewModelScope.launch {
        repo.deleteRoute(route)
    }
}

class RouteViewModelFactory(
    private val app: TourGuideApp
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RouteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RouteViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}
