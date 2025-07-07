package com.example.tourguideplus

import android.app.Application
import com.example.tourguideplus.data.AppDatabase

class TourGuideApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}
