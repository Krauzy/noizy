package com.noizy.application.dto

data class TrackUploadResponse(
    val track: TrackResponse,
    val audioS3Key: String,
    val coverS3Key: String?
)
