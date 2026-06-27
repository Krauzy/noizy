package com.noizy.interfaces.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class AlbumRequest(
    @field:NotBlank
    @field:Size(max = 220)
    val title: String,
    @field:NotNull
    val artistId: UUID,
    val coverUrl: String? = null,
    val releaseDate: LocalDate? = null
)

data class AlbumResponse(
    val id: UUID,
    val title: String,
    val artist: ArtistResponse,
    val coverUrl: String?,
    val releaseDate: LocalDate?,
    val createdAt: Instant,
    val updatedAt: Instant
)
