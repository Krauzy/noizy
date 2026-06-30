package com.noizy.adapters.output.persistence.repository

import com.noizy.domain.model.PlaylistTrackEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface PlaylistTrackJpaRepository : JpaRepository<PlaylistTrackEntity, UUID> {
    fun findByPlaylistIdOrderByPositionAsc(playlistId: UUID): List<PlaylistTrackEntity>
    fun countByPlaylistId(playlistId: UUID): Long
    fun existsByPlaylistIdAndTrackId(playlistId: UUID, trackId: UUID): Boolean

    @Modifying
    @Query("delete from PlaylistTrackEntity pt where pt.playlist.id = :playlistId and pt.track.id = :trackId")
    fun deleteByPlaylistIdAndTrackId(@Param("playlistId") playlistId: UUID, @Param("trackId") trackId: UUID): Int
}
