package com.noizy.application.dto

import java.time.Instant
import java.util.UUID

data class ArtistResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
