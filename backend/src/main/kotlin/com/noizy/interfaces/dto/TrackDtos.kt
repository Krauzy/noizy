package com.noizy.interfaces.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class TrackRequest(
    @field:NotBlank
    @field:Size(max = 220)
    val title: String,
    @field:NotNull
    val artistId: UUID,
    val albumId: UUID? = null,
    @field:Size(max = 120)
    val genre: String? = null,
    @field:Min(0)
    val durationSeconds: Int = 0,
    @field:NotBlank
    val audioS3Key: String,
    val coverS3Key: String? = null
)

data class TrackUpdateRequest(
    @field:NotBlank
    @field:Size(max = 220)
    val title: String,
    val albumId: UUID? = null,
    @field:Size(max = 120)
    val genre: String? = null,
    @field:Min(0)
    val durationSeconds: Int = 0,
    val coverS3Key: String? = null
)

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
    val liked: Boolean,
    val streamUrl: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class TrackUploadResponse(
    val track: TrackResponse,
    val audioS3Key: String,
    val coverS3Key: String?
)

data class TrackStreamResult(
    val content: java.io.InputStream,
    val contentType: String,
    val contentLength: Long,
    val statusCode: Int,
    val contentRange: String?
)

data class TrackCoverResult(
    val content: java.io.InputStream,
    val contentType: String,
    val contentLength: Long
)

data class TrackCommentRequest(
    @field:NotBlank
    @field:Size(max = 1200)
    val body: String
)

data class TrackCommentResponse(
    val id: UUID,
    val trackId: UUID,
    val user: UserResponse,
    val body: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
