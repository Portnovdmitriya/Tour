package com.example.tourguideplus.ui.profile

import androidx.lifecycle.*
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.model.UserEntity
import kotlinx.coroutines.launch

class ProfileViewModel(application: TourGuideApp) : AndroidViewModel(application) {

    private val repo = application.userRepository

    // Читаем single-user LiveData из репозитория
    val user: LiveData<UserEntity?> = repo.user

    // Сохранение / обновление профиля
    fun saveProfile(name: String, email: String) = viewModelScope.launch {
        val u = UserEntity(id = "default", name = name, email = email)
        repo.upsert(u)
    }
}

class ProfileViewModelFactory(
    private val app: TourGuideApp
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}
