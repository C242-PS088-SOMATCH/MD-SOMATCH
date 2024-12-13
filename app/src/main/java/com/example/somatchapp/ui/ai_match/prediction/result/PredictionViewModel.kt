package com.example.somatchapp.ui.ai_match.prediction.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somatchapp.data.local.entity.OutfitStyle
import com.example.somatchapp.data.repository.ModelRepository
import com.example.somatchapp.data.repository.OutfitStyleRepository
import kotlinx.coroutines.launch

class PredictionViewModel(
    private val modelRepository: ModelRepository,
    private val outfitStyleRepository: OutfitStyleRepository
) : ViewModel() {

    private val _predictionResult = MutableLiveData<String>()
    val predictionResult: LiveData<String> = _predictionResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for all outfit styles
    val allOutfitStyles: LiveData<List<OutfitStyle>> = outfitStyleRepository.allOutfitStyles

    // Function to predict the outfit
    fun predictOutfit(bearerToken: String, upper: String, bottom: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = modelRepository.predictOutfit(bearerToken, upper, bottom)
            if (result != null) {
                _predictionResult.value = result.result
            } else {
                _predictionResult.value = "Error: Unable to fetch prediction"
            }
            _isLoading.value = false
        }
    }
}
