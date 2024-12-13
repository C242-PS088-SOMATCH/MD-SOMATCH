package com.example.somatchapp.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfileResponse (
    val error: Boolean,
    val message: String,
    val data: List<UserProfileData>
): Parcelable

@Parcelize
data class UserProfileData (
    val id: Int,
    val name: String,
    val email: String,
    val username: String
):Parcelable