package com.example.somatchapp.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyCatalogResponse (
    val error: Boolean,
    val message: String,
    val id: String,
    val userId: String,
    val imageUrl: String,
    val createdAt: String
):Parcelable