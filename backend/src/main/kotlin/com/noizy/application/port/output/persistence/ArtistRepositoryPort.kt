package com.noizy.application.port.output.persistence

import com.noizy.domain.model.ArtistEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional
import java.util.UUID

interface ArtistRepositoryPort {
    fun save(artist: ArtistEntity): ArtistEntity
    fun findAll(pageable: Pageable): Page<ArtistEntity>
    fun findById(id: UUID): Optional<ArtistEntity>
    fun existsById(id: UUID): Boolean
    fun deleteById(id: UUID)
    fun findFirstByNameIgnoreCase(name: String): Optional<ArtistEntity>
}
