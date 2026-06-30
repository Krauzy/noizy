package com.noizy.application.port.output.persistence

import com.noizy.domain.model.PlaylistEntity
import java.util.Optional
import java.util.UUID

interface PlaylistRepositoryPort {
    fun save(playlist: PlaylistEntity): PlaylistEntity
    fun findByOwnerIdOrderByCreatedAtDesc(ownerId: UUID): List<PlaylistEntity>
    fun findByIsPublicTrueOrderByCreatedAtDesc(): List<PlaylistEntity>
    fun findById(id: UUID): Optional<PlaylistEntity>
}
