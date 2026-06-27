package com.noizy.interfaces.rest

import com.noizy.application.service.AlbumService
import com.noizy.interfaces.dto.AlbumRequest
import com.noizy.interfaces.dto.AlbumResponse
import com.noizy.interfaces.dto.TrackResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/albums")
class AlbumController(
    private val albumService: AlbumService
) {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun create(@Valid @RequestBody request: AlbumRequest): AlbumResponse =
        albumService.create(request)

    @GetMapping
    fun list(pageable: Pageable): Page<AlbumResponse> =
        albumService.list(pageable)

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): AlbumResponse =
        albumService.get(id)

    @GetMapping("/{id}/tracks")
    fun tracks(@PathVariable id: UUID): List<TrackResponse> =
        albumService.tracksForAlbum(id)

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: AlbumRequest): AlbumResponse =
        albumService.update(id, request)

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) =
        albumService.delete(id)
}
