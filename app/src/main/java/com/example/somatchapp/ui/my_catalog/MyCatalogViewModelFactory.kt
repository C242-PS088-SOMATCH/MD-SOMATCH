package com.example.somatchapp.ui.my_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.somatchapp.data.repository.MyCatalogRepository

class MyCatalogViewModelFactory(
    private val repository: MyCatalogRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyCatalogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyCatalogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
