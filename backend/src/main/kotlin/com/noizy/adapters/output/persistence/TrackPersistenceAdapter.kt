package com.noizy.adapters.output.persistence

import com.noizy.adapters.output.persistence.repository.TrackJpaRepository
import com.noizy.application.port.output.persistence.TrackRepositoryPort
import com.noizy.domain.model.TrackEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class TrackPersistenceAdapter(
    private val repository: TrackJpaRepository
) : TrackRepositoryPort {
    override fun save(track: TrackEntity): TrackEntity = repository.save(track)
    override fun findAll(pageable: Pageable): Page<TrackEntity> = repository.findAll(pageable)
    override fun search(query: String, pageable: Pageable): Page<TrackEntity> = repository.search(query, pageable)
    override fun findByAlbumId(albumId: UUID): List<TrackEntity> = repository.findByAlbumId(albumId)
    override fun findById(id: UUID): Optional<TrackEntity> = repository.findById(id)
    override fun existsById(id: UUID): Boolean = repository.existsById(id)
    override fun deleteById(id: UUID) = repository.deleteById(id)
}
