package com.example.somatchapp.data

import com.example.somatchapp.data.remote.retrofit.ApiService
import com.example.somatchapp.data.remote.retrofit.LoginRequest
import com.example.somatchapp.data.remote.retrofit.LoginResponse
import com.example.somatchapp.data.remote.retrofit.RegisterRequest
import com.example.somatchapp.data.remote.retrofit.RegisterResponse
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val loginRequest = LoginRequest(email, password)
        return apiService.login(loginRequest) // Return Response<LoginResponse>
    }

    suspend fun register(name: String, email: String, password: String): Response<RegisterResponse> {
        val registerRequest = RegisterRequest(name, email, password)
        return apiService.register(registerRequest) // Return Response<RegisterResponse>
    }
}