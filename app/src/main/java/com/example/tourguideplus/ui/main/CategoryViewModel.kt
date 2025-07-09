package com.example.tourguideplus.ui.main

import androidx.lifecycle.*
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.CategoryEntity
import com.example.tourguideplus.data.model.PlaceWithCategories
import kotlinx.coroutines.launch

class CategoryViewModel(app: TourGuideApp) : AndroidViewModel(app) {
    private val repo = app.categoryRepository

    val allCategories: LiveData<List<CategoryEntity>> = repo.all

    fun addCategory(name: String) = viewModelScope.launch {
        repo.add(name)
    }

    fun assignToPlace(placeId: Long, categoryIds: List<Long>) = viewModelScope.launch {
        repo.assignCategories(placeId, categoryIds)
    }
    suspend fun getPlaceWithCategories(placeId: Long): PlaceWithCategories? =
        repo.getPlaceWithCategories(placeId)

    fun updateCategory(cat: CategoryEntity) = viewModelScope.launch {
        repo.update(cat)
    }

    fun deleteCategory(cat: CategoryEntity) = viewModelScope.launch {
        repo.delete(cat)
    }
}

class CategoryViewModelFactory(
    private val app: TourGuideApp
) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(cls: Class<T>): T {
        if (cls.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}
