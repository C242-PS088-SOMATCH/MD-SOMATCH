package com.example.somatchapp.ui.ai_match.styling

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.somatchapp.data.repository.OutfitStyleRepository

class StylingViewModelFactory(
    private val application: Application,
    private val repository: OutfitStyleRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StylingViewModel::class.java)) {
            StylingViewModel(application, repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
