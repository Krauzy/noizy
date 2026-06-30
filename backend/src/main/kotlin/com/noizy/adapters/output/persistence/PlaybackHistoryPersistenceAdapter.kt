package com.noizy.adapters.output.persistence

import com.noizy.adapters.output.persistence.repository.PlaybackHistoryJpaRepository
import com.noizy.application.port.output.persistence.PlaybackHistoryRepositoryPort
import com.noizy.domain.model.PlaybackHistoryEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PlaybackHistoryPersistenceAdapter(
    private val repository: PlaybackHistoryJpaRepository
) : PlaybackHistoryRepositoryPort {
    override fun save(playbackHistory: PlaybackHistoryEntity): PlaybackHistoryEntity =
        repository.save(playbackHistory)

    override fun findTop50ByUserIdOrderByPlayedAtDesc(userId: UUID): List<PlaybackHistoryEntity> =
        repository.findTop50ByUserIdOrderByPlayedAtDesc(userId)
}
