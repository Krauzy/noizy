package com.noizy.infrastructure.persistence.repository

import com.noizy.infrastructure.persistence.entity.LikedTrackEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface LikedTrackJpaRepository : JpaRepository<LikedTrackEntity, UUID> {
    fun existsByUserIdAndTrackId(userId: UUID, trackId: UUID): Boolean
    fun findByUserIdOrderByCreatedAtDesc(userId: UUID): List<LikedTrackEntity>

    @Modifying
    @Query("delete from LikedTrackEntity lt where lt.user.id = :userId and lt.track.id = :trackId")
    fun deleteByUserIdAndTrackId(@Param("userId") userId: UUID, @Param("trackId") trackId: UUID): Int
}
