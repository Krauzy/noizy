package com.noizy.application.port.output.persistence

import com.noizy.domain.model.LikedTrackEntity
import java.util.UUID

interface LikedTrackRepositoryPort {
    fun existsByUserIdAndTrackId(userId: UUID, trackId: UUID): Boolean
    fun save(likedTrack: LikedTrackEntity): LikedTrackEntity
    fun deleteByUserIdAndTrackId(userId: UUID, trackId: UUID)
    fun findByUserIdOrderByCreatedAtDesc(userId: UUID): List<LikedTrackEntity>
}
