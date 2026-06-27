package com.noizy.interfaces.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class ArtistRequest(
    @field:NotBlank
    @field:Size(max = 180)
    val name: String,
    @field:Size(max = 2000)
    val description: String? = null,
    val imageUrl: String? = null
)

data class ArtistResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
