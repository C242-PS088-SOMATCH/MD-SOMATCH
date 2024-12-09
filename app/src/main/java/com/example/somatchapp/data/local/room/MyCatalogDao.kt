package com.example.somatchapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.somatchapp.data.local.entity.MyCatalog

@Dao
interface MyCatalogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(myCatalog: MyCatalog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(myCatalog: List<MyCatalog>)

    @Update
    suspend fun update(myCatalog: MyCatalog)

    @Delete
    suspend fun delete(myCatalog: MyCatalog)

    @Query("SELECT * FROM MyCatalog")
    fun getAllMyCatalog(): LiveData<List<MyCatalog>>

    @Query("SELECT * FROM MyCatalog WHERE id = :id")
    fun getMyCatalogById(id: Int): LiveData<MyCatalog?>

    @Query("SELECT * FROM MyCatalog WHERE inStyle = 0")
    fun getAllOutOfStyle(): LiveData<List<MyCatalog>>

    @Query("DELETE FROM MyCatalog")
    suspend fun deleteAll()

    @Query("UPDATE mycatalog SET isSelected = 0")
    suspend fun resetSelection()

    @Query("UPDATE MyCatalog SET inStyle = 1 WHERE id = :id")
    suspend fun setInStyle(id: Int)

    @Update
    suspend fun updateMyCatalog(myCatalog: MyCatalog)

    @Query("SELECT * FROM mycatalog WHERE isSelected = 1 LIMIT 1")
    suspend fun getSelectedMyCatalog(): MyCatalog?
}