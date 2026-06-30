package com.noizy.adapters.output.persistence.repository

import com.noizy.domain.model.AlbumEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlbumJpaRepository : JpaRepository<AlbumEntity, UUID> {
    fun findByArtistId(artistId: UUID): List<AlbumEntity>
}
