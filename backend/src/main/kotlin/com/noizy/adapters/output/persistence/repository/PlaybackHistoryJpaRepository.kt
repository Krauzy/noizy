package com.noizy.adapters.output.persistence.repository

import com.noizy.domain.model.PlaybackHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlaybackHistoryJpaRepository : JpaRepository<PlaybackHistoryEntity, UUID> {
    fun findTop50ByUserIdOrderByPlayedAtDesc(userId: UUID): List<PlaybackHistoryEntity>
}
