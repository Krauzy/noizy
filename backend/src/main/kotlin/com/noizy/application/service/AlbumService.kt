package com.noizy.application.service

import com.noizy.domain.exception.NotFoundException
import com.noizy.infrastructure.persistence.entity.AlbumEntity
import com.noizy.infrastructure.persistence.repository.AlbumJpaRepository
import com.noizy.interfaces.dto.AlbumRequest
import com.noizy.interfaces.dto.AlbumResponse
import com.noizy.interfaces.dto.TrackResponse
import com.noizy.interfaces.mapper.toResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AlbumService(
    private val albums: AlbumJpaRepository,
    private val artistService: ArtistService,
    private val tracks: com.noizy.infrastructure.persistence.repository.TrackJpaRepository
) {
    @Transactional
    fun create(request: AlbumRequest): AlbumResponse =
        albums.save(
            AlbumEntity(
                title = request.title.trim(),
                artist = artistService.getEntity(request.artistId),
                coverUrl = request.coverUrl?.trim(),
                releaseDate = request.releaseDate
            )
        ).toResponse()

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<AlbumResponse> =
        albums.findAll(pageable).map { it.toResponse() }

    @Transactional(readOnly = true)
    fun get(id: UUID): AlbumResponse = getEntity(id).toResponse()

    @Transactional(readOnly = true)
    fun tracksForAlbum(id: UUID): List<TrackResponse> {
        if (!albums.existsById(id)) throw NotFoundException("Album")
        return tracks.findByAlbumId(id).map { it.toResponse() }
    }

    @Transactional
    fun update(id: UUID, request: AlbumRequest): AlbumResponse {
        val album = getEntity(id)
        album.title = request.title.trim()
        album.artist = artistService.getEntity(request.artistId)
        album.coverUrl = request.coverUrl?.trim()
        album.releaseDate = request.releaseDate
        return album.toResponse()
    }

    @Transactional
    fun delete(id: UUID) {
        if (!albums.existsById(id)) throw NotFoundException("Album")
        albums.deleteById(id)
    }

    fun getEntity(id: UUID): AlbumEntity =
        albums.findById(id).orElseThrow { NotFoundException("Album") }
}
