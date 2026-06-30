package com.noizy.application.port.output.persistence

import com.noizy.domain.model.TrackEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional
import java.util.UUID

interface TrackRepositoryPort {
    fun save(track: TrackEntity): TrackEntity
    fun findAll(pageable: Pageable): Page<TrackEntity>
    fun search(query: String, pageable: Pageable): Page<TrackEntity>
    fun findByAlbumId(albumId: UUID): List<TrackEntity>
    fun findById(id: UUID): Optional<TrackEntity>
    fun existsById(id: UUID): Boolean
    fun deleteById(id: UUID)
}
