package com.example.somatchapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.somatchapp.data.local.dao.OutfitStyleDao
import com.example.somatchapp.data.local.entity.MyCatalog
import com.example.somatchapp.data.local.entity.OutfitStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MyCatalog::class, OutfitStyle::class],
    version = 1,
    exportSchema = false
)
abstract class MyCatalogRoomDatabase : RoomDatabase() {

    abstract fun myCatalogDao(): MyCatalogDao
    abstract fun outfitStyleDao(): OutfitStyleDao

    companion object {
        @Volatile
        private var INSTANCE: MyCatalogRoomDatabase? = null

        fun getDatabase(context: Context): MyCatalogRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyCatalogRoomDatabase::class.java,
                    "my_catalog_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
