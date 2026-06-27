package com.noizy.infrastructure.persistence.repository

import com.noizy.infrastructure.persistence.entity.ArtistEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ArtistJpaRepository : JpaRepository<ArtistEntity, UUID>
