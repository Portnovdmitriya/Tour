
package com.example.tourguideplus.data.auth

import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class AuthResponse(
    val ok: Boolean,
    val message: String? = null,
    val userId: String? = null,
    val username: String? = null
)

interface AuthService {
    @FormUrlEncoded
    @POST("exec")
    suspend fun register(
        @Field("action") action: String = "register",
        @Field("username") username: String,
        @Field("password") password: String
    ): AuthResponse

    @FormUrlEncoded
    @POST("exec")
    suspend fun login(
        @Field("action") action: String = "login",
        @Field("username") username: String,
        @Field("password") password: String
    ): AuthResponse

    companion object {
        fun create(baseUrl: String): AuthService =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthService::class.java)
    }
}
