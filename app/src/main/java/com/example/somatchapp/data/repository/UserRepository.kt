package com.example.somatchapp.data

import com.example.somatchapp.data.remote.request.LoginRequest
import com.example.somatchapp.data.remote.request.RegisterRequest
import com.example.somatchapp.data.remote.response.LoginResponse
import com.example.somatchapp.data.remote.response.RegisterResponse
import com.example.somatchapp.data.remote.retrofit.ApiService
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val loginRequest = LoginRequest(email, password)
        return apiService.login(loginRequest) // Return Response<LoginResponse>
    }

    suspend fun register(name: String, username: String, email: String, password: String): Response<RegisterResponse> {
        val registerRequest = RegisterRequest(name, username, email, password)
        return apiService.register(registerRequest) // Return Response<RegisterResponse>
    }
}