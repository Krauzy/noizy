package com.noizy.adapters.input.web

import com.noizy.adapters.output.security.UserPrincipal
import com.noizy.application.dto.PlaybackHistoryResponse
import com.noizy.application.dto.TrackResponse
import com.noizy.application.dto.TrackUpdateRequest
import com.noizy.application.dto.TrackUploadResponse
import com.noizy.application.port.input.TrackUseCase
import jakarta.validation.Valid
import org.springframework.core.io.InputStreamResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/tracks")
class TrackController(
    private val trackUseCase: TrackUseCase
) {
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ARTIST') or hasRole('ADMIN')")
    fun upload(
        @RequestParam title: String,
        @RequestParam(required = false) albumId: UUID?,
        @RequestParam(required = false) genre: String?,
        @RequestPart("audio") audio: MultipartFile,
        @RequestPart("cover", required = false) cover: MultipartFile?,
        @AuthenticationPrincipal principal: UserPrincipal
    ): TrackUploadResponse =
        trackUseCase.upload(title, albumId, genre, audio, cover, principal.id)

    @GetMapping
    fun list(pageable: Pageable): Page<TrackResponse> =
        trackUseCase.list(pageable)

    @GetMapping("/search")
    fun search(@RequestParam query: String, pageable: Pageable): Page<TrackResponse> =
        trackUseCase.search(query, pageable)

    @GetMapping("/liked")
    fun liked(@AuthenticationPrincipal principal: UserPrincipal): List<TrackResponse> =
        trackUseCase.liked(principal.id)

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): TrackResponse =
        trackUseCase.get(id)

    @GetMapping("/{id}/stream")
    fun stream(
        @PathVariable id: UUID,
        @RequestHeader(HttpHeaders.RANGE, required = false) range: String?,
        @AuthenticationPrincipal principal: UserPrincipal?
    ): ResponseEntity<InputStreamResource> {
        val result = trackUseCase.stream(id, range, principal?.id)
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(result.contentType)
        headers.contentLength = result.contentLength
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes")
        result.contentRange?.let { headers.set(HttpHeaders.CONTENT_RANGE, it) }
        return ResponseEntity(InputStreamResource(result.content), headers, HttpStatus.valueOf(result.statusCode))
    }

    @GetMapping("/{id}/cover")
    fun cover(@PathVariable id: UUID): ResponseEntity<InputStreamResource> {
        val result = trackUseCase.cover(id)
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(result.contentType)
        headers.contentLength = result.contentLength
        return ResponseEntity(InputStreamResource(result.content), headers, HttpStatus.OK)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @org.springframework.web.bind.annotation.RequestBody request: TrackUpdateRequest): TrackResponse =
        trackUseCase.update(id, request)

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) =
        trackUseCase.delete(id)

    @PostMapping("/{id}/like")
    fun like(@PathVariable id: UUID, @AuthenticationPrincipal principal: UserPrincipal): TrackResponse =
        trackUseCase.like(id, principal.id)

    @DeleteMapping("/{id}/like")
    fun unlike(@PathVariable id: UUID, @AuthenticationPrincipal principal: UserPrincipal) =
        trackUseCase.unlike(id, principal.id)
}
