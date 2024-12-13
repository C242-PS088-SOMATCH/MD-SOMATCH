package com.example.somatchapp.ui.ai_match.prediction.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.somatchapp.data.repository.ModelRepository
import com.example.somatchapp.data.repository.OutfitStyleRepository

class PredictionViewModelFactory(
    private val modelRepository: ModelRepository,
    private val outfitStyleRepository: OutfitStyleRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PredictionViewModel::class.java)) {
            return PredictionViewModel(modelRepository, outfitStyleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

