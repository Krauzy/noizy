package com.noizy.adapters.input.web

import com.noizy.adapters.output.security.UserPrincipal
import com.noizy.application.dto.UserResponse
import com.noizy.application.port.input.AuthUseCase
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val authUseCase: AuthUseCase
) {
    @GetMapping("/me")
    fun me(@AuthenticationPrincipal principal: UserPrincipal): UserResponse =
        authUseCase.me(principal.id)
}
