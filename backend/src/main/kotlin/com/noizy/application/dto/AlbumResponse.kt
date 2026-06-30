package com.noizy.application.dto

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class AlbumResponse(
    val id: UUID,
    val title: String,
    val artist: ArtistResponse,
    val coverUrl: String?,
    val releaseDate: LocalDate?,
    val createdAt: Instant,
    val updatedAt: Instant
)
