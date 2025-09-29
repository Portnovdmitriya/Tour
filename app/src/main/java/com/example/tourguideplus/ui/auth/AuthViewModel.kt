package com.example.tourguideplus.ui.auth

import androidx.lifecycle.*
import com.example.tourguideplus.TourGuideApp
import com.example.tourguideplus.data.auth.AuthRepository
import com.example.tourguideplus.data.auth.AuthService
import com.example.tourguideplus.data.model.SettingEntity
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle: AuthState()
    object Loading: AuthState()
    data class Success(val username: String): AuthState()
    data class Error(val message: String): AuthState()
}

class AuthViewModel(private val app: TourGuideApp): ViewModel() {
    private val settings = app.settingRepository

    private val baseUrl = app.getString(com.example.tourguideplus.R.string.gs_base_url)
    private val repo = AuthRepository(AuthService.create(baseUrl))

    private val _state = MutableLiveData<AuthState>(AuthState.Idle)
    val state: LiveData<AuthState> = _state

    // LiveData текущего пользователя (или null)
    val currentUser: LiveData<String?> = settings.getSetting("auth_user").map { it?.value }

    fun login(username: String, password: String) = viewModelScope.launch {
        _state.value = AuthState.Loading
        try {
            val resp = repo.login(username, password)
            if (resp.ok && (resp.username ?: username).isNotBlank()) {
                settings.upsert(SettingEntity("auth_user", resp.username ?: username))
                _state.value = AuthState.Success(resp.username ?: username)
            } else {
                _state.value = AuthState.Error(resp.message ?: "Ошибка входа")
            }
        } catch (e: Exception) {
            _state.value = AuthState.Error(e.message ?: "Сеть недоступна")
        }
    }

    fun register(username: String, password: String) = viewModelScope.launch {
        _state.value = AuthState.Loading
        try {
            val resp = repo.register(username, password)
            if (resp.ok && (resp.username ?: username).isNotBlank()) {
                settings.upsert(SettingEntity("auth_user", resp.username ?: username))
                _state.value = AuthState.Success(resp.username ?: username)
            } else {
                _state.value = AuthState.Error(resp.message ?: "Ошибка регистрации")
            }
        } catch (e: Exception) {
            _state.value = AuthState.Error(e.message ?: "Сеть недоступна")
        }
    }

    fun logout() = viewModelScope.launch {
        settings.deleteByKey("auth_user")
        _state.value = AuthState.Idle
    }
}

class AuthViewModelFactory(private val app: TourGuideApp): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(c: Class<T>): T {
        if (c.isAssignableFrom(AuthViewModel::class.java)) return AuthViewModel(app) as T
        throw IllegalArgumentException("Unknown VM")
    }
}
