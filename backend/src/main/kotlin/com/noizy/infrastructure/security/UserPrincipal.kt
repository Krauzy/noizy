package com.noizy.infrastructure.security

import com.noizy.infrastructure.persistence.entity.UserRole
import java.util.UUID

data class UserPrincipal(
    val id: UUID,
    val email: String,
    val role: UserRole
)
