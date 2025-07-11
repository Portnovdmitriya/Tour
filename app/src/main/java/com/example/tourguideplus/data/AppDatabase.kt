package com.example.tourguideplus.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tourguideplus.data.dao.CategoryDao
import com.example.tourguideplus.data.dao.PlaceDao
import com.example.tourguideplus.data.dao.RouteDao
import com.example.tourguideplus.data.model.CategoryEntity
import com.example.tourguideplus.data.model.FavoriteEntity
import com.example.tourguideplus.data.model.NoteEntity
import com.example.tourguideplus.data.model.PlaceCategoryCrossRef
import com.example.tourguideplus.data.model.PlaceEntity
import com.example.tourguideplus.data.model.RouteEntity
import com.example.tourguideplus.data.model.RoutePlaceCrossRef
import com.example.tourguideplus.data.model.SettingEntity
import com.example.tourguideplus.data.model.UserEntity
import com.example.tourguideplus.data.model.WeatherCacheEntity

@Database(
    entities = [
        PlaceEntity::class,
        CategoryEntity::class,
        PlaceCategoryCrossRef::class,
        RouteEntity::class,
        RoutePlaceCrossRef::class,
        FavoriteEntity::class,
        NoteEntity::class,
        WeatherCacheEntity::class,
        UserEntity::class,
        SettingEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao
    abstract fun routeDao(): RouteDao
    abstract fun categoryDao(): CategoryDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tourguideplus_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
