package com.example.somatchapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.somatchapp.data.local.entity.OutfitStyle

@Dao
interface OutfitStyleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(outfitStyle: OutfitStyle): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(outfitStyles: List<OutfitStyle>)

    @Update
    suspend fun update(outfitStyle: OutfitStyle)

    @Delete
    suspend fun delete(outfitStyle: OutfitStyle)

    @Query("SELECT * FROM OutfitStyle")
    fun getAllOutfitStyles(): LiveData<List<OutfitStyle>>

    @Query("SELECT * FROM OutfitStyle WHERE id = :id")
    fun getOutfitStyleById(id: Int): LiveData<OutfitStyle?>

    @Query("DELETE FROM OutfitStyle")
    suspend fun deleteAll()

    @Query("SELECT * FROM OutfitStyle WHERE type = :type")
    suspend fun getOutfitStylesByType(type: String): List<OutfitStyle>

    @Query("DELETE FROM OutfitStyle WHERE id = :id")
    suspend fun deleteById(id: Int)
}
