package com.example.somatchapp.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ItemsResponse(
    val message: String,
    val data: List<AllItemResponse>
)

@Parcelize
data class AllItemResponse(
    val image_url: String,
    val category: String,
    val dominant_color: String,
    val style: String,
    val color_group: String,
    val name: String
) : Parcelable {
    // Convert the dominant_color string to a List<Double> after the object is created
    val dominantColorList: List<Double>
        get() {
            return try {
                // Remove the brackets and split by space
                dominant_color
                    .removeSurrounding("[", "]")
                    .split(" ")
                    .mapNotNull { it.toDoubleOrNull() }
            } catch (e: Exception) {
                emptyList()
            }
        }
}