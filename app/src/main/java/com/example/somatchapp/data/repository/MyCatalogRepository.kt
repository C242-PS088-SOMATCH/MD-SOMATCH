package com.example.somatchapp.data.repository

import android.content.Context
import com.example.somatchapp.data.remote.response.AllItemResponse
import com.example.somatchapp.data.remote.response.MyCatalogResponse
import com.example.somatchapp.data.remote.response.UploadImageResponse
import com.example.somatchapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCatalogRepository(private val apiService: ApiService) {

    suspend fun getAllItems(context: Context): Result<List<AllItemResponse>> {
        return try {
            val token = getTokenFromSharedPreferences(context) ?: ""
            // Make the network call asynchronously
            val response = withContext(Dispatchers.IO) {
                apiService.getAllItem("Bearer $token").execute()
            }

            if (response.isSuccessful) {
                // Extract the 'data' from ItemsResponse and return it
                val itemsResponse = response.body()
                if (itemsResponse != null) {
                    Result.success(itemsResponse.data)
                } else {
                    Result.failure(Exception("Empty response data"))
                }
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getTokenFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }

    fun getMyCatalog(token: String, onResult: (Result<MyCatalogResponse>) -> Unit) {
        val call = apiService.getMyCatalog("Bearer $token")

        call.enqueue(object : Callback<MyCatalogResponse> {
            override fun onResponse(
                call: Call<MyCatalogResponse>,
                response: Response<MyCatalogResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onResult(Result.success(responseBody))
                    } else {
                        onResult(Result.failure(Throwable("Response body is null")))
                    }
                } else {
                    onResult(Result.failure(Throwable("Error: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<MyCatalogResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

    suspend fun uploadImageToMyCatalog(
        bearerToken: String,
        image: MultipartBody.Part,
        type: String
    ): Response<UploadImageResponse> {
        return apiService.uploadImageToMyCatalog(bearerToken, image, type)
    }

}
