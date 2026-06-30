package com.noizy.application.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
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
