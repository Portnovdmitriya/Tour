// app/src/main/java/com/example/tourguideplus/ui/routes/RouteViewModel.kt
package com.example.tourguideplus.ui.routes

import androidx.lifecycle.*
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.RouteEntity
import com.example.tourguideplus.data.model.RouteWithPlaces
import kotlinx.coroutines.launch

class RouteViewModel(app: TourGuideApp) : AndroidViewModel(app) {
    private val repo = app.routeRepository

    val routesWithPlaces: LiveData<List<RouteWithPlaces>> = repo.allRoutesWithPlaces

    private val routeIdLive = MutableLiveData<Long>()
    val selected: LiveData<RouteWithPlaces?> =
        routeIdLive.switchMap { id -> repo.observeRouteWithPlaces(id) }

    fun select(routeId: Long) {
        routeIdLive.value = routeId
    }

    fun createRoute(name: String, desc: String?, placeIds: List<Long>) = viewModelScope.launch {
        val route = RouteEntity(name = name, description = desc)
        val newId = repo.createRouteWithPlaces(route, placeIds)
        // при создании тоже можно сразу “подписаться”
        select(newId)
    }

    fun updateRoute(route: RouteEntity, placeIds: List<Long>) = viewModelScope.launch {
        repo.updateRouteWithPlaces(route, placeIds)
        // selected обновится сам, потому что LiveData из БД
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
