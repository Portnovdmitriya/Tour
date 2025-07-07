package com.example.tourguideplus

import android.app.Application
import com.example.tourguideplus.data.AppDatabase
import com.example.tourguideplus.data.repository.PlaceRepository

class TourGuideApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val placeRepository: PlaceRepository by lazy {
        PlaceRepository(database.placeDao())
    }
}
