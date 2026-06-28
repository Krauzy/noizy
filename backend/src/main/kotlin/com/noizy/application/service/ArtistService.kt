package com.noizy.application.service

import com.noizy.domain.exception.NotFoundException
import com.noizy.infrastructure.persistence.entity.ArtistEntity
import com.noizy.infrastructure.persistence.entity.UserEntity
import com.noizy.infrastructure.persistence.repository.ArtistJpaRepository
import com.noizy.interfaces.dto.ArtistRequest
import com.noizy.interfaces.dto.ArtistResponse
import com.noizy.interfaces.mapper.toResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ArtistService(
    private val artists: ArtistJpaRepository
) {
    @Transactional
    fun create(request: ArtistRequest): ArtistResponse =
        artists.save(
            ArtistEntity(
                name = request.name.trim(),
                description = request.description?.trim(),
                imageUrl = request.imageUrl?.trim()
            )
        ).toResponse()

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<ArtistResponse> =
        artists.findAll(pageable).map { it.toResponse() }

    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<ArtistResponse> =
        artists.search(query.trim(), pageable).map { it.toResponse() }

    @Transactional(readOnly = true)
    fun get(id: UUID): ArtistResponse = getEntity(id).toResponse()

    @Transactional
    fun update(id: UUID, request: ArtistRequest): ArtistResponse {
        val artist = getEntity(id)
        artist.name = request.name.trim()
        artist.description = request.description?.trim()
        artist.imageUrl = request.imageUrl?.trim()
        return artist.toResponse()
    }

    @Transactional
    fun delete(id: UUID) {
        if (!artists.existsById(id)) throw NotFoundException("Artist")
        artists.deleteById(id)
    }

    fun getOrCreateForUploader(user: UserEntity): ArtistEntity =
        artists.findFirstByNameIgnoreCase(user.name).orElseGet {
            artists.save(
                ArtistEntity(
                    name = user.name,
                    description = "Artist profile for ${user.email}",
                    imageUrl = null
                )
            )
        }

    fun getEntity(id: UUID): ArtistEntity =
        artists.findById(id).orElseThrow { NotFoundException("Artist") }
}
