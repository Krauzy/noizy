package com.noizy.adapters.input.web

import com.noizy.application.dto.ArtistRequest
import com.noizy.application.dto.ArtistResponse
import com.noizy.application.port.input.ArtistUseCase
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
@RequestMapping("/api/artists")
class ArtistController(
    private val artistUseCase: ArtistUseCase
) {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun create(@Valid @RequestBody request: ArtistRequest): ArtistResponse =
        artistUseCase.create(request)

    @GetMapping
    fun list(pageable: Pageable): Page<ArtistResponse> =
        artistUseCase.list(pageable)

    @GetMapping("/search")
    fun search(@org.springframework.web.bind.annotation.RequestParam query: String, pageable: Pageable): Page<ArtistResponse> =
        artistService.search(query, pageable)

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ArtistResponse =
        artistUseCase.get(id)

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: ArtistRequest): ArtistResponse =
        artistUseCase.update(id, request)

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) =
        artistUseCase.delete(id)
}
