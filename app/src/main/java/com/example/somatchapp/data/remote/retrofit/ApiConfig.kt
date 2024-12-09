package com.example.somatchapp.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {

    private val BASE_URL = "https://cc-somatch-555454916720.asia-southeast2.run.app/"

    fun getApiService(): ApiService {
        // Logging interceptor to monitor API calls
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // OkHttpClient for custom configurations
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Log request/response
            .connectTimeout(30, TimeUnit.SECONDS) // Set connection timeout
            .readTimeout(30, TimeUnit.SECONDS) // Set read timeout
            .writeTimeout(30, TimeUnit.SECONDS) // Set write timeout
            .build()

        // Build Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // Set base URL
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
            .client(client) // Use custom client
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
