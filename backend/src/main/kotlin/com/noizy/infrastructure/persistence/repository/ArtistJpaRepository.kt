package com.noizy.infrastructure.persistence.repository

import com.noizy.infrastructure.persistence.entity.ArtistEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

interface ArtistJpaRepository : JpaRepository<ArtistEntity, UUID> {
    fun findFirstByNameIgnoreCase(name: String): Optional<ArtistEntity>

    @Query(
        """
        select a from ArtistEntity a
        where lower(a.name) like lower(concat('%', :query, '%'))
           or lower(coalesce(a.description, '')) like lower(concat('%', :query, '%'))
        """
    )
    fun search(@Param("query") query: String, pageable: Pageable): Page<ArtistEntity>
}
