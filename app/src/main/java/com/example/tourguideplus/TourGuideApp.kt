package com.example.tourguideplus

import android.app.Application
import com.example.tourguideplus.data.AppDatabase
import com.example.tourguideplus.data.repository.CategoryRepository
import com.example.tourguideplus.data.repository.FavoriteRepository
import com.example.tourguideplus.data.repository.NoteRepository
import com.example.tourguideplus.data.repository.PlaceRepository
import com.example.tourguideplus.data.repository.RouteRepository
import com.example.tourguideplus.data.repository.SettingRepository
import com.example.tourguideplus.data.repository.UserRepository
import com.example.tourguideplus.data.repository.WeatherCacheRepository

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
    val favoriteRepository: FavoriteRepository by lazy {
        FavoriteRepository(database.favoriteDao())
    }
    val noteRepository: NoteRepository by lazy {
        NoteRepository(database.noteDao())
    }
    val weatherCacheRepository: WeatherCacheRepository by lazy {
        WeatherCacheRepository(database.weatherCacheDao())
    }
    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }
    val settingRepository: SettingRepository by lazy {
        SettingRepository(database.settingDao())
    }
}
