package com.example.somatchapp.ui.ai_match.styling
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somatchapp.data.local.OutfitStyleRoomDatabase
import com.example.somatchapp.data.local.entity.OutfitStyle
import com.example.somatchapp.data.repository.OutfitStyleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StylingViewModel(application: Application, private val outfitStyleRepository: OutfitStyleRepository) : AndroidViewModel(application) {

    private val outfitStyleDao = OutfitStyleRoomDatabase.getDatabase(application).outfitStyleDao()

    // LiveData for observing all OutfitStyle data
    val allOutfitStyles: LiveData<List<OutfitStyle>> = outfitStyleRepository.allOutfitStyles

    // Function to insert a new OutfitStyle
    fun insert(outfitStyle: OutfitStyle) {
        viewModelScope.launch(Dispatchers.IO) {
            outfitStyleRepository.insert(outfitStyle)
        }
    }

    // Function to insert multiple OutfitStyles
    fun insertAll(outfitStyles: List<OutfitStyle>) {
        viewModelScope.launch(Dispatchers.IO) {
            outfitStyleRepository.insertAll(outfitStyles)
        }
    }

    // Function to update an OutfitStyle
    fun update(outfitStyle: OutfitStyle) {
        viewModelScope.launch(Dispatchers.IO) {
            outfitStyleRepository.update(outfitStyle)
        }
    }

    // Function to delete an OutfitStyle
    fun delete(outfitStyle: OutfitStyle) {
        viewModelScope.launch(Dispatchers.IO) {
            outfitStyleRepository.delete(outfitStyle)
        }
    }

    // Function to delete all OutfitStyles
    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            outfitStyleRepository.deleteAll()
        }
    }

    // Function to get a specific OutfitStyle by ID
    fun getOutfitStyleById(id: Int): LiveData<OutfitStyle?> {
        return outfitStyleRepository.getOutfitStyleById(id)
    }
}