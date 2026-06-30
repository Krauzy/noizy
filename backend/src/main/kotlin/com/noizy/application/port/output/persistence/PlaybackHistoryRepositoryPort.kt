package com.noizy.application.port.output.persistence

import com.noizy.domain.model.PlaybackHistoryEntity
import java.util.UUID

interface PlaybackHistoryRepositoryPort {
    fun save(playbackHistory: PlaybackHistoryEntity): PlaybackHistoryEntity
    fun findTop50ByUserIdOrderByPlayedAtDesc(userId: UUID): List<PlaybackHistoryEntity>
}
