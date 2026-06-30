package com.noizy.adapters.output.persistence

import com.noizy.adapters.output.persistence.repository.AlbumJpaRepository
import com.noizy.application.port.output.persistence.AlbumRepositoryPort
import com.noizy.domain.model.AlbumEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class AlbumPersistenceAdapter(
    private val repository: AlbumJpaRepository
) : AlbumRepositoryPort {
    override fun save(album: AlbumEntity): AlbumEntity = repository.save(album)
    override fun findAll(pageable: Pageable): Page<AlbumEntity> = repository.findAll(pageable)
    override fun findById(id: UUID): Optional<AlbumEntity> = repository.findById(id)
    override fun existsById(id: UUID): Boolean = repository.existsById(id)
    override fun deleteById(id: UUID) = repository.deleteById(id)
}
