package com.noizy.adapters.output.persistence

import com.noizy.adapters.output.persistence.repository.PlaylistJpaRepository
import com.noizy.application.port.output.persistence.PlaylistRepositoryPort
import com.noizy.domain.model.PlaylistEntity
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class PlaylistPersistenceAdapter(
    private val repository: PlaylistJpaRepository
) : PlaylistRepositoryPort {
    override fun save(playlist: PlaylistEntity): PlaylistEntity = repository.save(playlist)
    override fun findByOwnerIdOrderByCreatedAtDesc(ownerId: UUID): List<PlaylistEntity> =
        repository.findByOwnerIdOrderByCreatedAtDesc(ownerId)

    override fun findByIsPublicTrueOrderByCreatedAtDesc(): List<PlaylistEntity> =
        repository.findByIsPublicTrueOrderByCreatedAtDesc()

    override fun findById(id: UUID): Optional<PlaylistEntity> = repository.findById(id)
}
