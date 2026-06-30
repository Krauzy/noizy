package com.noizy.application.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

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
