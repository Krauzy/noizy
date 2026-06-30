package com.noizy.adapters.input.web

import com.noizy.adapters.output.security.UserPrincipal
import com.noizy.application.dto.AuthResponse
import com.noizy.application.dto.LoginRequest
import com.noizy.application.dto.RegisterRequest
import com.noizy.application.dto.UserResponse
import com.noizy.application.port.input.AuthUseCase
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authUseCase: AuthUseCase
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): AuthResponse =
        authUseCase.register(request)

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse =
        authUseCase.login(request)

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal principal: UserPrincipal): UserResponse =
        authUseCase.me(principal.id)
}
