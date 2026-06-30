package com.noizy.application.port.input

import com.noizy.application.dto.ArtistRequest
import com.noizy.application.dto.ArtistResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ArtistUseCase {
    fun create(request: ArtistRequest): ArtistResponse
    fun list(pageable: Pageable): Page<ArtistResponse>
    fun get(id: UUID): ArtistResponse
    fun update(id: UUID, request: ArtistRequest): ArtistResponse
    fun delete(id: UUID)
}
