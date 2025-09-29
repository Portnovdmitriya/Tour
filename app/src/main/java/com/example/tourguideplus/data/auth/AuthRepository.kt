package com.example.tourguideplus.data.auth

class AuthRepository(private val api: AuthService) {
    suspend fun register(username: String, password: String) = api.register(username = username, password = password)
    suspend fun login(username: String, password: String)    = api.login(username = username, password = password)
}
