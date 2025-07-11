package com.example.tourguideplus.data.repository

import androidx.lifecycle.LiveData
import com.example.tourguideplus.data.dao.UserDao
import com.example.tourguideplus.data.model.UserEntity

class UserRepository(private val dao: UserDao) {

    /** Профиль пользователя (единственная запись) */
    val user: LiveData<UserEntity?> = dao.getUser()

    /** Сохранить или обновить профиль */
    suspend fun upsert(user: UserEntity) {
        dao.upsert(user)
    }
}
