package com.noizy.interfaces.rest

import com.noizy.application.service.AuthService
import com.noizy.infrastructure.security.UserPrincipal
import com.noizy.interfaces.dto.UserResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val authService: AuthService
) {
    @GetMapping("/me")
    fun me(@AuthenticationPrincipal principal: UserPrincipal): UserResponse =
        authService.me(principal.id)
}
