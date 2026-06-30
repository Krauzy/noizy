package com.noizy.adapters.output.persistence.repository

import com.noizy.domain.model.ArtistEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ArtistJpaRepository : JpaRepository<ArtistEntity, UUID> {
    fun findFirstByNameIgnoreCase(name: String): Optional<ArtistEntity>
}
