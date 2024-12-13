package com.example.somatchapp.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somatchapp.data.remote.response.AllItemResponse
import com.example.somatchapp.data.repository.MyCatalogRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val myCatalogRepository: MyCatalogRepository) : ViewModel() {

    private val _items = MutableLiveData<List<AllItemResponse>>()
    val items: LiveData<List<AllItemResponse>> get() = _items

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Function to fetch items
    fun fetchItems(context: Context) {
        viewModelScope.launch {
            // Call the repository to get the items in a background thread
            val result = myCatalogRepository.getAllItems(context) // Pass context to fetch token

            // Handle the result
            result.onSuccess {
                _items.value = it
            }
            result.onFailure {
                _errorMessage.value = it.localizedMessage ?: "Unknown error"
            }
        }
    }
}