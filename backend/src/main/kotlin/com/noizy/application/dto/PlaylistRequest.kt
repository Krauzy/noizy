package com.noizy.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PlaylistRequest(
    @field:NotBlank
    @field:Size(max = 180)
    val name: String,
    @field:Size(max = 2000)
    val description: String? = null,
    val isPublic: Boolean = false
)
