package com.example.tourguideplus

import android.app.Application
import com.example.tourguideplus.data.AppDatabase
import com.example.tourguideplus.data.repository.CategoryRepository
import com.example.tourguideplus.data.repository.PlaceRepository
import com.example.tourguideplus.data.repository.RouteRepository

class TourGuideApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val placeRepository: PlaceRepository by lazy {
        PlaceRepository(database.placeDao())
    }
    val routeRepository: RouteRepository by lazy {
        RouteRepository(database.routeDao())
    }
    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(database.categoryDao())
    }
}
