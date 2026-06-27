package com.noizy.infrastructure.persistence.repository

import com.noizy.infrastructure.persistence.entity.PlaylistEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlaylistJpaRepository : JpaRepository<PlaylistEntity, UUID> {
    fun findByOwnerIdOrderByCreatedAtDesc(ownerId: UUID): List<PlaylistEntity>
    fun findByIsPublicTrueOrderByCreatedAtDesc(): List<PlaylistEntity>
}
