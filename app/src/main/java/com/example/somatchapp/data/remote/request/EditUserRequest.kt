package com.example.somatchapp.data.remote.request

data class EditUserRequest(
    val name: String,
    val username: String,
    val email: String,
    val password: String
)