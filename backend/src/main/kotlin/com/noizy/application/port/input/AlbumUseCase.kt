package com.noizy.application.port.input

import com.noizy.application.dto.AlbumRequest
import com.noizy.application.dto.AlbumResponse
import com.noizy.application.dto.TrackResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface AlbumUseCase {
    fun create(request: AlbumRequest): AlbumResponse
    fun list(pageable: Pageable): Page<AlbumResponse>
    fun get(id: UUID): AlbumResponse
    fun tracksForAlbum(id: UUID): List<TrackResponse>
    fun update(id: UUID, request: AlbumRequest): AlbumResponse
    fun delete(id: UUID)
}
