package com.noizy.application.port.output.persistence

import com.noizy.domain.model.PlaylistTrackEntity
import java.util.UUID

interface PlaylistTrackRepositoryPort {
    fun existsByPlaylistIdAndTrackId(playlistId: UUID, trackId: UUID): Boolean
    fun countByPlaylistId(playlistId: UUID): Long
    fun save(playlistTrack: PlaylistTrackEntity): PlaylistTrackEntity
    fun deleteByPlaylistIdAndTrackId(playlistId: UUID, trackId: UUID)
    fun findByPlaylistIdOrderByPositionAsc(playlistId: UUID): List<PlaylistTrackEntity>
}
