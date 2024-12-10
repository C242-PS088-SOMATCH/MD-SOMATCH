package com.example.somatchapp.data.remote.retrofit

import com.example.somatchapp.data.remote.request.EditUserRequest
import com.example.somatchapp.data.remote.request.LoginRequest
import com.example.somatchapp.data.remote.request.RegisterRequest
import com.example.somatchapp.data.remote.response.DeleteUserResponse
import com.example.somatchapp.data.remote.response.EditUserResponse
import com.example.somatchapp.data.remote.response.LoginResponse
import com.example.somatchapp.data.remote.response.MyCatalogResponse
import com.example.somatchapp.data.remote.response.RegisterResponse
import com.example.somatchapp.data.remote.response.UploadImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

data class User(val id: Int, val name: String, val username: String, val email: String, val password: String)

interface ApiService {
    @POST("api/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/users/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @GET("api/mycatalog")
    fun getMyCatalog(
        @Header("Authorization") bearerToken: String
    ): Call<MyCatalogResponse>

    // Endpoint untuk mengedit pengguna
    @PATCH("api/users/edit")
    suspend fun editUser(
        @Header("Authorization") bearerToken: String,
        @Body editRequest: EditUserRequest
    ): Response<EditUserResponse>

    // Endpoint untuk menghapus pengguna
    @DELETE("api/users/delete")
    suspend fun deleteUser(
        @Header("Authorization") bearerToken: String
    ): Response<DeleteUserResponse>

    // Endpoint untuk meng-upload gambar ke My Catalog
    @POST("api/mycatalog/upload")
    suspend fun uploadImageToMyCatalog(
        @Header("Authorization") bearerToken: String,
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>
}

