package com.example.somatchapp.data.remote.request

data class RegisterRequest(
    val name: String,
    val email: String,
    val username: String,
    val password: String
)