package com.noizy.application.service

import com.noizy.domain.exception.NotFoundException
import com.noizy.domain.model.AlbumEntity
import com.noizy.application.dto.AlbumRequest
import com.noizy.application.dto.AlbumResponse
import com.noizy.application.dto.TrackResponse
import com.noizy.application.mapper.toResponse
import com.noizy.application.port.input.AlbumUseCase
import com.noizy.application.port.output.persistence.AlbumRepositoryPort
import com.noizy.application.port.output.persistence.TrackRepositoryPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AlbumService(
    private val albums: AlbumRepositoryPort,
    private val artistService: ArtistService,
    private val tracks: TrackRepositoryPort
) : AlbumUseCase {
    @Transactional
    override fun create(request: AlbumRequest): AlbumResponse =
        albums.save(
            AlbumEntity(
                title = request.title.trim(),
                artist = artistService.getEntity(request.artistId),
                coverUrl = request.coverUrl?.trim(),
                releaseDate = request.releaseDate
            )
        ).toResponse()

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): Page<AlbumResponse> =
        albums.findAll(pageable).map { it.toResponse() }

    @Transactional(readOnly = true)
    override fun get(id: UUID): AlbumResponse = getEntity(id).toResponse()

    @Transactional(readOnly = true)
    override fun tracksForAlbum(id: UUID): List<TrackResponse> {
        if (!albums.existsById(id)) throw NotFoundException("Album")
        return tracks.findByAlbumId(id).map { it.toResponse() }
    }

    @Transactional
    override fun update(id: UUID, request: AlbumRequest): AlbumResponse {
        val album = getEntity(id)
        album.title = request.title.trim()
        album.artist = artistService.getEntity(request.artistId)
        album.coverUrl = request.coverUrl?.trim()
        album.releaseDate = request.releaseDate
        return album.toResponse()
    }

    @Transactional
    override fun delete(id: UUID) {
        if (!albums.existsById(id)) throw NotFoundException("Album")
        albums.deleteById(id)
    }

    fun getEntity(id: UUID): AlbumEntity =
        albums.findById(id).orElseThrow { NotFoundException("Album") }
}
