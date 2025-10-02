package com.example.tourguideplus.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tourguideplus.data.dao.*
import com.example.tourguideplus.data.model.*

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
    version = 28,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao
    abstract fun routeDao(): RouteDao
    abstract fun categoryDao(): CategoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun noteDao(): NoteDao
    abstract fun weatherCacheDao(): WeatherCacheDao
    abstract fun userDao(): UserDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null


        private val MIGRATION_25_26 = object : Migration(25, 26) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // добавляем столбец, если его нет
                val c = db.query("PRAGMA table_info(places)")
                var hasCreatedAt = false
                val nameIdx = c.getColumnIndex("name")
                while (c.moveToNext()) {
                    val col = if (nameIdx >= 0) c.getString(nameIdx) else c.getString(1)
                    if (col.equals("createdAt", ignoreCase = true)) { hasCreatedAt = true; break }
                }
                c.close()
                if (!hasCreatedAt) {
                    db.execSQL(
                        "ALTER TABLE places " +
                                "ADD COLUMN createdAt INTEGER NOT NULL " +
                                "DEFAULT (strftime('%s','now')*1000)"
                    )
                }
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tourguideplus_db"
                )
                    // Если база была 25 → мигрируем аккуратно, без потери
                    .addMigrations(MIGRATION_25_26)
                    // Если база более старая (1..24) → пересоздать (чтобы не писать длинную цепочку миграций)
                    .fallbackToDestructiveMigrationFrom(*(1..24).toList().toIntArray())
                    // Если вдруг на устройстве стоит БОЛЕЕ НОВАЯ версия (downgrade) → пересоздать вместо краша
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}

