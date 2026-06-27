package com.noizy.interfaces.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class PlaylistRequest(
    @field:NotBlank
    @field:Size(max = 180)
    val name: String,
    @field:Size(max = 2000)
    val description: String? = null,
    val isPublic: Boolean = false
)

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

data class PlaybackHistoryResponse(
    val id: UUID,
    val track: TrackResponse,
    val playedAt: Instant
)
