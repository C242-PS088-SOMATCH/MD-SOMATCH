package com.example.somatchapp.data.repository

import com.example.somatchapp.data.remote.request.PredictRequest
import com.example.somatchapp.data.remote.retrofit.ApiService
import com.example.somatchapp.data.remote.retrofit.PredictionResponse

class ModelRepository(private val apiService: ApiService) {

    suspend fun predictOutfit(bearerToken: String, upper: String, bottom: String): PredictionResponse? {
        val predictRequest = PredictRequest(upper, bottom)
        val response = apiService.predictModel(bearerToken, predictRequest)

        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}
