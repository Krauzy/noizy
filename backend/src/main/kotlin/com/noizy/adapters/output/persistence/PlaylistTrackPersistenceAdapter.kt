package com.noizy.adapters.output.persistence

import com.noizy.adapters.output.persistence.repository.PlaylistTrackJpaRepository
import com.noizy.application.port.output.persistence.PlaylistTrackRepositoryPort
import com.noizy.domain.model.PlaylistTrackEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PlaylistTrackPersistenceAdapter(
    private val repository: PlaylistTrackJpaRepository
) : PlaylistTrackRepositoryPort {
    override fun existsByPlaylistIdAndTrackId(playlistId: UUID, trackId: UUID): Boolean =
        repository.existsByPlaylistIdAndTrackId(playlistId, trackId)

    override fun countByPlaylistId(playlistId: UUID): Long = repository.countByPlaylistId(playlistId)
    override fun save(playlistTrack: PlaylistTrackEntity): PlaylistTrackEntity = repository.save(playlistTrack)
    override fun deleteByPlaylistIdAndTrackId(playlistId: UUID, trackId: UUID) {
        repository.deleteByPlaylistIdAndTrackId(playlistId, trackId)
    }

    override fun findByPlaylistIdOrderByPositionAsc(playlistId: UUID): List<PlaylistTrackEntity> =
        repository.findByPlaylistIdOrderByPositionAsc(playlistId)
}
