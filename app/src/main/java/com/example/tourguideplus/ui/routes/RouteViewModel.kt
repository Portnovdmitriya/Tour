// app/src/main/java/com/example/tourguideplus/ui/routes/RouteViewModel.kt
package com.example.tourguideplus.ui.routes

import androidx.lifecycle.*
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.RouteEntity
import kotlinx.coroutines.launch

class RouteViewModel(app: TourGuideApp) : AndroidViewModel(app) {
    private val repo = app.routeRepository

    val routes: LiveData<List<RouteEntity>> = repo.allRoutes

    private val _selected = MutableLiveData<RouteEntity?>()
    val selected: LiveData<RouteEntity?> = _selected

    fun select(route: RouteEntity) { _selected.value = route }

    fun add(route: RouteEntity) = viewModelScope.launch { repo.insert(route) }
    fun update(route: RouteEntity) = viewModelScope.launch { repo.update(route) }
    fun delete(route: RouteEntity) = viewModelScope.launch { repo.delete(route) }
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
