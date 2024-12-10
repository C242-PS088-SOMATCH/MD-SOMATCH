package com.example.somatchapp.data.remote.response

import com.example.somatchapp.data.remote.retrofit.User

data class RegisterResponse(
    val message: String,
    val user: User
)