package com.example.somatchapp.data.repository

import androidx.lifecycle.LiveData
import com.example.somatchapp.data.local.dao.OutfitStyleDao
import com.example.somatchapp.data.local.entity.OutfitStyle

class OutfitStyleRepository(private val outfitStyleDao: OutfitStyleDao) {

    // LiveData for observing all OutfitStyle data
    val allOutfitStyles: LiveData<List<OutfitStyle>> = outfitStyleDao.getAllOutfitStyles()

    // Insert a single OutfitStyle
    suspend fun insert(outfitStyle: OutfitStyle) {
        outfitStyleDao.insert(outfitStyle)
    }

    // Insert multiple OutfitStyles
    suspend fun insertAll(outfitStyles: List<OutfitStyle>) {
        outfitStyleDao.insertAll(outfitStyles)
    }

    // Update an OutfitStyle
    suspend fun update(outfitStyle: OutfitStyle) {
        outfitStyleDao.update(outfitStyle)
    }

    // Delete a specific OutfitStyle
    suspend fun delete(outfitStyle: OutfitStyle) {
        outfitStyleDao.delete(outfitStyle)
    }

    // Delete all OutfitStyles
    suspend fun deleteAll() {
        outfitStyleDao.deleteAll()
    }

    // Get a specific OutfitStyle by ID
    fun getOutfitStyleById(id: Int): LiveData<OutfitStyle?> {
        return outfitStyleDao.getOutfitStyleById(id)
    }
}
