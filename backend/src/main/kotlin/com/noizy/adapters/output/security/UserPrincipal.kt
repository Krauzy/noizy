package com.noizy.adapters.output.security

import com.noizy.domain.model.UserRole
import java.util.UUID

data class UserPrincipal(
    val id: UUID,
    val email: String,
    val role: UserRole
)
