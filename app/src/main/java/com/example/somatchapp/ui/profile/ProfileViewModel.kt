package com.example.somatchapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somatchapp.data.UserRepository
import com.example.somatchapp.data.remote.request.EditUserRequest
import com.example.somatchapp.data.remote.response.DeleteUserResponse
import com.example.somatchapp.data.remote.response.EditUserResponse
import com.example.somatchapp.data.remote.response.UserProfileResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    val editUserResponse = MutableLiveData<Response<EditUserResponse>>()
    val deleteUserResponse = MutableLiveData<Response<DeleteUserResponse>>()
    val userProfileResponse = MutableLiveData<Response<UserProfileResponse>>()

    // Fungsi untuk mengedit pengguna
    fun editUser(bearerToken: String, editRequest: EditUserRequest) {
        viewModelScope.launch {
            val response = userRepository.editUser(bearerToken, editRequest)
            editUserResponse.postValue(response)
        }
    }

    // Fungsi untuk menghapus pengguna
    fun deleteUser(bearerToken: String) {
        viewModelScope.launch {
            val response = userRepository.deleteUser(bearerToken)
            deleteUserResponse.postValue(response)
        }
    }

    // Fungsi untuk mengambil profil pengguna
    fun getUserProfile(bearerToken: String) {
        viewModelScope.launch {
            val response = userRepository.getUserProfile(bearerToken)
            userProfileResponse.postValue(response)
        }
    }
}
