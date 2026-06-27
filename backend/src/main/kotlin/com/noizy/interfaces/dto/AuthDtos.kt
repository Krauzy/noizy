package com.noizy.interfaces.dto

import com.noizy.infrastructure.persistence.entity.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class RegisterRequest(
    @field:NotBlank
    @field:Size(min = 2, max = 160)
    val name: String,

    @field:Email
    @field:NotBlank
    val email: String,

    @field:Size(min = 8, max = 120)
    val password: String
)

data class LoginRequest(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: UserResponse
)

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val role: UserRole,
    val createdAt: Instant
)
