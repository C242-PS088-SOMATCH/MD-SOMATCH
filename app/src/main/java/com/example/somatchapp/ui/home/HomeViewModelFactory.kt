package com.example.somatchapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.somatchapp.data.repository.MyCatalogRepository

class HomeViewModelFactory(
    private val myCatalogRepository: MyCatalogRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(myCatalogRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
