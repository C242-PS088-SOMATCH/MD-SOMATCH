package com.example.somatchapp.data

import com.example.somatchapp.data.remote.request.EditUserRequest
import com.example.somatchapp.data.remote.request.LoginRequest
import com.example.somatchapp.data.remote.request.RegisterRequest
import com.example.somatchapp.data.remote.response.DeleteUserResponse
import com.example.somatchapp.data.remote.response.EditUserResponse
import com.example.somatchapp.data.remote.response.LoginResponse
import com.example.somatchapp.data.remote.response.RegisterResponse
import com.example.somatchapp.data.remote.response.UserProfileResponse
import com.example.somatchapp.data.remote.retrofit.ApiService
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val loginRequest = LoginRequest(email, password)
        return apiService.login(loginRequest) // Return Response<LoginResponse>
    }

    suspend fun register(name: String, email: String, username: String, password: String): Response<RegisterResponse> {
        val registerRequest = RegisterRequest(name, email, username, password)
        return apiService.register(registerRequest)
    }

    suspend fun editUser(bearerToken: String, editRequest: EditUserRequest): Response<EditUserResponse> {
        return apiService.editUser("Bearer $bearerToken", editRequest)
    }

    suspend fun deleteUser(bearerToken: String): Response<DeleteUserResponse> {
        return apiService.deleteUser("Bearer $bearerToken")
    }

    suspend fun getUserProfile(bearerToken: String): Response<UserProfileResponse> {
        return apiService.getUserProfile("Bearer $bearerToken")
    }
}

