package com.noizy.adapters.output.persistence

import com.noizy.adapters.output.persistence.repository.ArtistJpaRepository
import com.noizy.application.port.output.persistence.ArtistRepositoryPort
import com.noizy.domain.model.ArtistEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class ArtistPersistenceAdapter(
    private val repository: ArtistJpaRepository
) : ArtistRepositoryPort {
    override fun save(artist: ArtistEntity): ArtistEntity = repository.save(artist)
    override fun findAll(pageable: Pageable): Page<ArtistEntity> = repository.findAll(pageable)
    override fun findById(id: UUID): Optional<ArtistEntity> = repository.findById(id)
    override fun existsById(id: UUID): Boolean = repository.existsById(id)
    override fun deleteById(id: UUID) = repository.deleteById(id)
    override fun findFirstByNameIgnoreCase(name: String): Optional<ArtistEntity> =
        repository.findFirstByNameIgnoreCase(name)
}
