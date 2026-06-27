package com.noizy.infrastructure.persistence.repository

import com.noizy.infrastructure.persistence.entity.AlbumEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AlbumJpaRepository : JpaRepository<AlbumEntity, UUID> {
    fun findByArtistId(artistId: UUID): List<AlbumEntity>
}
