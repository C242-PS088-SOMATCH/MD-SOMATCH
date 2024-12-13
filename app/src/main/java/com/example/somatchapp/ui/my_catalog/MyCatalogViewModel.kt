package com.example.somatchapp.ui.my_catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.somatchapp.data.remote.response.MyCatalog
import com.example.somatchapp.data.repository.MyCatalogRepository

class MyCatalogViewModel(private val repository: MyCatalogRepository) : ViewModel() {

    private val _catalogList = MutableLiveData<List<MyCatalog>>()
    val catalogList: LiveData<List<MyCatalog>> get() = _catalogList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchMyCatalog(token: String) {
        repository.getMyCatalog(token) { result ->
            result.onSuccess { response ->
                _catalogList.postValue(response.data)
            }
            result.onFailure { throwable ->
                _error.postValue("Error: ${throwable.message}")
            }
        }
    }
}

