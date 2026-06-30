package com.noizy.application.dto

import java.time.Instant
import java.util.UUID

data class TrackResponse(
    val id: UUID,
    val title: String,
    val artist: ArtistResponse,
    val album: AlbumResponse?,
    val genre: String?,
    val durationSeconds: Int,
    val audioS3Key: String,
    val coverS3Key: String?,
    val playCount: Long,
    val streamUrl: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
