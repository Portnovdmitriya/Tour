package com.example.tourguideplus.data.repository

import androidx.lifecycle.LiveData
import com.example.tourguideplus.data.dao.SettingDao
import com.example.tourguideplus.data.model.SettingEntity

class SettingRepository(private val dao: SettingDao) {

    /** Все настройки */
    val allSettings: LiveData<List<SettingEntity>> = dao.getAll()

    /** Установить (или обновить) настройку */
    suspend fun upsert(setting: SettingEntity) {
        dao.upsert(setting)
    }

    /** Удалить настройку по ключу */
    suspend fun deleteByKey(key: String) {
        dao.deleteByKey(key)
    }
}
