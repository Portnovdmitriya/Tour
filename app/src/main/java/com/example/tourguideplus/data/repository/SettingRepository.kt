package com.example.tourguideplus.data.repository

import androidx.lifecycle.LiveData
import com.example.tourguideplus.data.dao.SettingDao
import com.example.tourguideplus.data.model.SettingEntity

class SettingRepository(private val dao: SettingDao) {

    val allSettings: LiveData<List<SettingEntity>> = dao.getAll()

    suspend fun upsert(setting: SettingEntity) {
        dao.upsert(setting)
    }

    suspend fun deleteByKey(key: String) {
        dao.deleteByKey(key)
    }

    fun getSetting(key: String): LiveData<SettingEntity?> =
        dao.getSetting(key)
}
