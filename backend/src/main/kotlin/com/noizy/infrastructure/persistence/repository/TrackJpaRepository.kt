package com.noizy.infrastructure.persistence.repository

import com.noizy.infrastructure.persistence.entity.TrackEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface TrackJpaRepository : JpaRepository<TrackEntity, UUID> {
    fun findByAlbumId(albumId: UUID): List<TrackEntity>

    @Query(
        """
        select t from TrackEntity t
        join t.artist a
        left join t.album al
        where lower(t.title) like lower(concat('%', :query, '%'))
           or lower(a.name) like lower(concat('%', :query, '%'))
           or lower(coalesce(al.title, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(t.genre, '')) like lower(concat('%', :query, '%'))
        """
    )
    fun search(@Param("query") query: String, pageable: Pageable): Page<TrackEntity>
}
