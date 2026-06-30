package com.noizy.application.port.output.persistence

import com.noizy.domain.model.AlbumEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional
import java.util.UUID

interface AlbumRepositoryPort {
    fun save(album: AlbumEntity): AlbumEntity
    fun findAll(pageable: Pageable): Page<AlbumEntity>
    fun findById(id: UUID): Optional<AlbumEntity>
    fun existsById(id: UUID): Boolean
    fun deleteById(id: UUID)
}
