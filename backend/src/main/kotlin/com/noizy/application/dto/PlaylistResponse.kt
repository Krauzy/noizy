package com.noizy.application.dto

import java.time.Instant
import java.util.UUID

data class PlaylistResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val owner: UserResponse,
    val isPublic: Boolean,
    val tracks: List<TrackResponse>,
    val createdAt: Instant,
    val updatedAt: Instant
)
