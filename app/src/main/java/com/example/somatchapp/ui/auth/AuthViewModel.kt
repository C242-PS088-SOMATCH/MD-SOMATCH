package com.example.somatchapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somatchapp.data.UserRepository
import com.example.somatchapp.data.remote.retrofit.LoginResponse
import com.example.somatchapp.data.remote.retrofit.RegisterResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun login(email: String, password: String, onResult: (Response<LoginResponse>) -> Unit) {
        viewModelScope.launch {
            try {
                // Call the login method from the repository and get the response
                val response = userRepository.login(email, password)
                // Pass the response back to the activity or fragment
                onResult(response)
            } catch (e: Exception) {
                // Handle any errors that occur during the API call
                val errorResponse = Response.error<LoginResponse>(
                    500, "Network error".toResponseBody("application/json".toMediaTypeOrNull())
                )
                onResult(errorResponse)  // Return the error response
            }
        }
    }

    fun register(name: String, email: String, password: String, onResult: (Response<RegisterResponse>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = userRepository.register(name, email, password)
                onResult(response)
            } catch (e: Exception) {
                // Handle error
                val errorResponse = Response.error<RegisterResponse>(
                    500, "Network error".toResponseBody("application/json".toMediaTypeOrNull())
                )
                onResult(errorResponse)
            }
        }
    }
}