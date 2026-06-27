package com.noizy.infrastructure.persistence.repository

import com.noizy.infrastructure.persistence.entity.PlaybackHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlaybackHistoryJpaRepository : JpaRepository<PlaybackHistoryEntity, UUID> {
    fun findTop50ByUserIdOrderByPlayedAtDesc(userId: UUID): List<PlaybackHistoryEntity>
}
