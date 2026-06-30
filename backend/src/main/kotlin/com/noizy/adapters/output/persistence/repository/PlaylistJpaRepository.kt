package com.noizy.adapters.output.persistence.repository

import com.noizy.domain.model.PlaylistEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PlaylistJpaRepository : JpaRepository<PlaylistEntity, UUID> {
    fun findByOwnerIdOrderByCreatedAtDesc(ownerId: UUID): List<PlaylistEntity>
    fun findByIsPublicTrueOrderByCreatedAtDesc(): List<PlaylistEntity>
}
