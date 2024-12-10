package com.example.somatchapp.data.local.entity
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class MyCatalog(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "image")
    var image: String? = null,

    @ColumnInfo(name = "isSelected")
    var isSelected: Boolean = false,

    @ColumnInfo(name = "inStyle")
    var inStyle: Boolean = false,
) : Parcelable
