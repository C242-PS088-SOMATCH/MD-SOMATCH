package com.example.somatchapp.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyCatalogResponse (
    val error: Boolean,
    val message: String,
    val data: List<MyCatalog>
):Parcelable {
}

@Parcelize
data class MyCatalog(
    val id: Int,
    val userId: Int,
    val type: String,
    val image_url: String,
    val created_at: String
) : Parcelable