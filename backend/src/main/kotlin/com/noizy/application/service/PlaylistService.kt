package com.noizy.application.service

import com.noizy.domain.event.NoizyEventFactory
import com.noizy.domain.exception.ConflictException
import com.noizy.domain.exception.ForbiddenException
import com.noizy.domain.exception.NotFoundException
import com.noizy.domain.model.PlaylistEntity
import com.noizy.domain.model.PlaylistTrackEntity
import com.noizy.application.dto.PlaylistRequest
import com.noizy.application.dto.PlaylistResponse
import com.noizy.application.mapper.toResponse
import com.noizy.application.port.input.PlaylistUseCase
import com.noizy.application.port.output.DomainEventPublisher
import com.noizy.application.port.output.persistence.PlaylistRepositoryPort
import com.noizy.application.port.output.persistence.PlaylistTrackRepositoryPort
import com.noizy.application.port.output.persistence.UserRepositoryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PlaylistService(
    private val playlists: PlaylistRepositoryPort,
    private val playlistTracks: PlaylistTrackRepositoryPort,
    private val users: UserRepositoryPort,
    private val trackService: TrackService,
    private val eventPublisher: DomainEventPublisher
) : PlaylistUseCase {
    @Transactional
    override fun create(request: PlaylistRequest, ownerId: UUID): PlaylistResponse {
        val owner = users.findById(ownerId).orElseThrow { NotFoundException("User") }
        val playlist = playlists.save(
            PlaylistEntity(
                name = request.name.trim(),
                description = request.description?.trim(),
                owner = owner,
                isPublic = request.isPublic
            )
        )
        eventPublisher.publish(NoizyEventFactory.playlistCreated(ownerId, playlist.id))
        return playlist.toResponse(emptyList())
    }

    @Transactional(readOnly = true)
    override fun mine(ownerId: UUID): List<PlaylistResponse> =
        playlists.findByOwnerIdOrderByCreatedAtDesc(ownerId).map { it.toResponse(tracksFor(it)) }

    @Transactional(readOnly = true)
    override fun publicPlaylists(): List<PlaylistResponse> =
        playlists.findByIsPublicTrueOrderByCreatedAtDesc().map { it.toResponse(tracksFor(it)) }

    @Transactional(readOnly = true)
    override fun get(id: UUID, requesterId: UUID?): PlaylistResponse {
        val playlist = getEntity(id)
        if (!playlist.isPublic && playlist.owner.id != requesterId) {
            throw ForbiddenException()
        }
        return playlist.toResponse(tracksFor(playlist))
    }

    @Transactional
    override fun update(id: UUID, request: PlaylistRequest, requesterId: UUID): PlaylistResponse {
        val playlist = getEntity(id)
        ensureOwner(playlist, requesterId)
        playlist.name = request.name.trim()
        playlist.description = request.description?.trim()
        playlist.isPublic = request.isPublic
        return playlist.toResponse(tracksFor(playlist))
    }

    @Transactional
    override fun addTrack(id: UUID, trackId: UUID, requesterId: UUID): PlaylistResponse {
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
        return playlist.toResponse(tracksFor(playlist))
    }

    @Transactional
    override fun removeTrack(id: UUID, trackId: UUID, requesterId: UUID): PlaylistResponse {
        val playlist = getEntity(id)
        ensureOwner(playlist, requesterId)
        playlistTracks.deleteByPlaylistIdAndTrackId(id, trackId)
        return playlist.toResponse(tracksFor(playlist))
    }

    fun getEntity(id: UUID): PlaylistEntity =
        playlists.findById(id).orElseThrow { NotFoundException("Playlist") }

    private fun tracksFor(playlist: PlaylistEntity): List<PlaylistTrackEntity> =
        playlistTracks.findByPlaylistIdOrderByPositionAsc(playlist.id ?: throw NotFoundException("Playlist id"))

    private fun ensureOwner(playlist: PlaylistEntity, requesterId: UUID) {
        if (playlist.owner.id != requesterId) throw ForbiddenException()
    }
}
