package com.noizy.application.dto

data class AuthResponse(
    val token: String,
    val user: UserResponse
)
