package com.example.somatchapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.somatchapp.data.local.dao.OutfitStyleDao
import com.example.somatchapp.data.local.entity.OutfitStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [OutfitStyle::class], version = 1, exportSchema = false)
abstract class OutfitStyleRoomDatabase : RoomDatabase() {

    abstract fun outfitStyleDao(): OutfitStyleDao

    companion object {
        @Volatile
        private var INSTANCE: OutfitStyleRoomDatabase? = null

        fun getDatabase(context: Context): OutfitStyleRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OutfitStyleRoomDatabase::class.java,
                    "somatch_app_database"
                )
                    .addCallback(RoomDatabaseCallback(context)) // Add callback for pre-population
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class RoomDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Insert pre-defined data when the database is created
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.outfitStyleDao())
                }
            }
        }

        suspend fun populateDatabase(outfitStyleDao: OutfitStyleDao) {
            // Predefined data to insert
            val predefinedData = listOf(
                OutfitStyle(1, "bag", null, null),
                OutfitStyle(2, "top wear", null, null),
                OutfitStyle(3, "accessories", null, null),
                OutfitStyle(4, "bottom wear", null, null),
                OutfitStyle(5, "footwear", null, null)
            )
            outfitStyleDao.insertAll(predefinedData)
        }
    }
}