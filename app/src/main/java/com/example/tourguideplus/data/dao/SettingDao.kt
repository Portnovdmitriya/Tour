package com.example.tourguideplus.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tourguideplus.data.model.SettingEntity

@Dao
interface SettingDao {
    @Query("SELECT * FROM settings")
    fun getAll(): LiveData<List<SettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(setting: SettingEntity)

    @Query("DELETE FROM settings WHERE key = :key")
    suspend fun deleteByKey(key: String)

    @Query("SELECT * FROM settings WHERE `key` = :key LIMIT 1")
    fun getSetting(key: String): LiveData<SettingEntity?>
}
