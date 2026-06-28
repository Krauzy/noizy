package com.noizy.infrastructure.persistence.repository

import com.noizy.infrastructure.persistence.entity.TrackCommentEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TrackCommentJpaRepository : JpaRepository<TrackCommentEntity, UUID> {
    fun findByTrackIdOrderByCreatedAtDesc(trackId: UUID): List<TrackCommentEntity>
}
