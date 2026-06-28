package com.noizy.application.service

import com.noizy.domain.event.NoizyEvent
import com.noizy.domain.event.NoizyEventType
import com.noizy.domain.exception.ConflictException
import com.noizy.domain.exception.ForbiddenException
import com.noizy.domain.exception.NotFoundException
import com.noizy.infrastructure.messaging.EventPublisher
import com.noizy.infrastructure.persistence.entity.PlaylistEntity
import com.noizy.infrastructure.persistence.entity.PlaylistTrackEntity
import com.noizy.infrastructure.persistence.repository.LikedTrackJpaRepository
import com.noizy.infrastructure.persistence.repository.PlaylistJpaRepository
import com.noizy.infrastructure.persistence.repository.PlaylistTrackJpaRepository
import com.noizy.infrastructure.persistence.repository.UserJpaRepository
import com.noizy.interfaces.dto.PlaylistRequest
import com.noizy.interfaces.dto.PlaylistResponse
import com.noizy.interfaces.mapper.toResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PlaylistService(
    private val playlists: PlaylistJpaRepository,
    private val playlistTracks: PlaylistTrackJpaRepository,
    private val users: UserJpaRepository,
    private val likes: LikedTrackJpaRepository,
    private val trackService: TrackService,
    private val eventPublisher: EventPublisher
) {
    @Transactional
    fun create(request: PlaylistRequest, ownerId: UUID): PlaylistResponse {
        val owner = users.findById(ownerId).orElseThrow { NotFoundException("User") }
        val playlist = playlists.save(
            PlaylistEntity(
                name = request.name.trim(),
                description = request.description?.trim(),
                owner = owner,
                isPublic = request.isPublic
            )
        )
        eventPublisher.publish(
            NoizyEvent(
                type = NoizyEventType.PLAYLIST_CREATED,
                actorUserId = ownerId,
                aggregateId = playlist.id
            )
        )
        return playlist.toResponse(emptyList())
    }

    @Transactional(readOnly = true)
    fun mine(ownerId: UUID): List<PlaylistResponse> =
        playlists.findByOwnerIdOrderByCreatedAtDesc(ownerId).map { it.toResponseFor(ownerId) }

    @Transactional(readOnly = true)
    fun publicPlaylists(): List<PlaylistResponse> =
        playlists.findByIsPublicTrueOrderByCreatedAtDesc().map { it.toResponse(tracksFor(it)) }

    @Transactional(readOnly = true)
    fun get(id: UUID, requesterId: UUID?): PlaylistResponse {
        val playlist = getEntity(id)
        if (!playlist.isPublic && playlist.owner.id != requesterId) {
            throw ForbiddenException()
        }
        return playlist.toResponseFor(requesterId)
    }

    @Transactional
    fun update(id: UUID, request: PlaylistRequest, requesterId: UUID): PlaylistResponse {
        val playlist = getEntity(id)
        ensureOwner(playlist, requesterId)
        playlist.name = request.name.trim()
        playlist.description = request.description?.trim()
        playlist.isPublic = request.isPublic
        return playlist.toResponseFor(requesterId)
    }

    @Transactional
    fun addTrack(id: UUID, trackId: UUID, requesterId: UUID): PlaylistResponse {
        val playlist = getEntity(id)
        ensureOwner(playlist, requesterId)
        if (playlistTracks.existsByPlaylistIdAndTrackId(id, trackId)) {
            throw ConflictException("Track already exists in playlist")
        }
        val position = playlistTracks.countByPlaylistId(id).toInt() + 1
        playlistTracks.save(
            PlaylistTrackEntity(
                playlist = playlist,
                track = trackService.getEntity(trackId),
                position = position
            )
        )
        return playlist.toResponseFor(requesterId)
    }

    @Transactional
    fun removeTrack(id: UUID, trackId: UUID, requesterId: UUID): PlaylistResponse {
        val playlist = getEntity(id)
        ensureOwner(playlist, requesterId)
        playlistTracks.deleteByPlaylistIdAndTrackId(id, trackId)
        return playlist.toResponseFor(requesterId)
    }

    fun getEntity(id: UUID): PlaylistEntity =
        playlists.findById(id).orElseThrow { NotFoundException("Playlist") }

    private fun tracksFor(playlist: PlaylistEntity): List<PlaylistTrackEntity> =
        playlistTracks.findByPlaylistIdOrderByPositionAsc(playlist.id ?: throw NotFoundException("Playlist id"))

    private fun PlaylistEntity.toResponseFor(requesterId: UUID?): PlaylistResponse {
        val tracks = tracksFor(this)
        val trackIds = tracks.mapNotNull { it.track.id }
        val likedIds = if (requesterId == null || trackIds.isEmpty()) {
            emptySet()
        } else {
            likes.findLikedTrackIds(requesterId, trackIds).toSet()
        }
        return toResponse(tracks, likedIds)
    }

    private fun ensureOwner(playlist: PlaylistEntity, requesterId: UUID) {
        if (playlist.owner.id != requesterId) throw ForbiddenException()
    }
}
