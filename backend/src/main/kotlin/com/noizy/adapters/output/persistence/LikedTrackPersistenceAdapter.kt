package com.noizy.adapters.output.persistence

import com.noizy.adapters.output.persistence.repository.LikedTrackJpaRepository
import com.noizy.application.port.output.persistence.LikedTrackRepositoryPort
import com.noizy.domain.model.LikedTrackEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class LikedTrackPersistenceAdapter(
    private val repository: LikedTrackJpaRepository
) : LikedTrackRepositoryPort {
    override fun existsByUserIdAndTrackId(userId: UUID, trackId: UUID): Boolean =
        repository.existsByUserIdAndTrackId(userId, trackId)

    override fun save(likedTrack: LikedTrackEntity): LikedTrackEntity = repository.save(likedTrack)

    override fun deleteByUserIdAndTrackId(userId: UUID, trackId: UUID) {
        repository.deleteByUserIdAndTrackId(userId, trackId)
    }

    override fun findByUserIdOrderByCreatedAtDesc(userId: UUID): List<LikedTrackEntity> =
        repository.findByUserIdOrderByCreatedAtDesc(userId)
}
