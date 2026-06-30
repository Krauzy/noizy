package com.noizy.application.port.input

import com.noizy.application.dto.AuthResponse
import com.noizy.application.dto.LoginRequest
import com.noizy.application.dto.RegisterRequest
import com.noizy.application.dto.UserResponse
import java.util.UUID

interface AuthUseCase {
    fun register(request: RegisterRequest): AuthResponse
    fun login(request: LoginRequest): AuthResponse
    fun me(userId: UUID): UserResponse
}
