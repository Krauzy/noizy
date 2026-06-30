package com.noizy.application.dto

import java.time.Instant
import java.util.UUID

data class PlaybackHistoryResponse(
    val id: UUID,
    val track: TrackResponse,
    val playedAt: Instant
)
