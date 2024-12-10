package com.example.somatchapp.data.remote.retrofit

import com.example.somatchapp.data.remote.response.MyCatalogResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)

data class LoginResponse(val message: String, val token: String)

data class RegisterResponse(val message: String, val user: User)
data class User(val id: Int, val name: String, val email: String, val password: String)

interface ApiService {
    @POST("api/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/users/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @GET("api/mycatalog")
    fun getMyCatalog(
        @Header("Authorization") bearerToken: String
    ): Call<MyCatalogResponse>
}
