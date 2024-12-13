package com.example.somatchapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyCombination(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "userId")
    var userId: Int = 0,

    @ColumnInfo(name = "hat")
    var hat: String? = null,

    @ColumnInfo(name = "upperwear")
    var upperwear: String? = null,

    @ColumnInfo(name = "bottomwear")
    var bottomwear: String? = null,

    @ColumnInfo(name = "bag")
    var bag: String? = null,

    @ColumnInfo(name = "footwear")
    var footwear: String? = null,

):Parcelable
