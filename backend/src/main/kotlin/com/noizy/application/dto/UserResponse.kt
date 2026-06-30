package com.noizy.application.dto

import com.noizy.domain.model.UserRole
import java.time.Instant
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val role: UserRole,
    val createdAt: Instant
)
