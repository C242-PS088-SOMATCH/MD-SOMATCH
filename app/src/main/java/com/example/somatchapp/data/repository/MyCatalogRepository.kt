package com.example.somatchapp.data.repository

import androidx.lifecycle.LiveData
import com.example.somatchapp.data.local.room.MyCatalogDao
import com.example.somatchapp.data.local.entity.MyCatalog
import com.example.somatchapp.data.remote.response.MyCatalogResponse
import com.example.somatchapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCatalogRepository(private val myCatalogDao: MyCatalogDao, private val apiService: ApiService) {

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

    // Get all catalog items
    val allMyCatalog: LiveData<List<MyCatalog>> = myCatalogDao.getAllMyCatalog()

    // Get catalog item by ID
    fun getMyCatalogById(id: Int): LiveData<MyCatalog?> = myCatalogDao.getMyCatalogById(id)

    // Get all out-of-style catalog items
    val allOutOfStyle: LiveData<List<MyCatalog>> = myCatalogDao.getAllOutOfStyle()

    // Insert a single catalog item
    suspend fun insert(myCatalog: MyCatalog) {
        withContext(Dispatchers.IO) {
            myCatalogDao.insert(myCatalog)
        }
    }

    // Insert multiple catalog items
    suspend fun insertAll(myCatalogList: List<MyCatalog>) {
        withContext(Dispatchers.IO) {
            myCatalogDao.insertAll(myCatalogList)
        }
    }

    // Update a catalog item
    suspend fun update(myCatalog: MyCatalog) {
        withContext(Dispatchers.IO) {
            myCatalogDao.update(myCatalog)
        }
    }

    // Delete a catalog item
    suspend fun delete(myCatalog: MyCatalog) {
        withContext(Dispatchers.IO) {
            myCatalogDao.delete(myCatalog)
        }
    }

    // Delete all catalog items
    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            myCatalogDao.deleteAll()
        }
    }
}
