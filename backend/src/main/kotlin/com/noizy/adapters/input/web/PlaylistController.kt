package com.noizy.adapters.input.web

import com.noizy.adapters.output.security.UserPrincipal
import com.noizy.application.dto.PlaylistRequest
import com.noizy.application.dto.PlaylistResponse
import com.noizy.application.port.input.PlaylistUseCase
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
@RequestMapping("/api/playlists")
class PlaylistController(
    private val playlistUseCase: PlaylistUseCase
) {
    @PostMapping
    fun create(@Valid @RequestBody request: PlaylistRequest, @AuthenticationPrincipal principal: UserPrincipal): PlaylistResponse =
        playlistUseCase.create(request, principal.id)

    @GetMapping("/me")
    fun mine(@AuthenticationPrincipal principal: UserPrincipal): List<PlaylistResponse> =
        playlistUseCase.mine(principal.id)

    @GetMapping("/public")
    fun publicPlaylists(): List<PlaylistResponse> =
        playlistUseCase.publicPlaylists()

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID, @AuthenticationPrincipal principal: UserPrincipal?): PlaylistResponse =
        playlistUseCase.get(id, principal?.id)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: PlaylistRequest,
        @AuthenticationPrincipal principal: UserPrincipal
    ): PlaylistResponse =
        playlistUseCase.update(id, request, principal.id)

    @PostMapping("/{id}/tracks/{trackId}")
    fun addTrack(
        @PathVariable id: UUID,
        @PathVariable trackId: UUID,
        @AuthenticationPrincipal principal: UserPrincipal
    ): PlaylistResponse =
        playlistUseCase.addTrack(id, trackId, principal.id)

    @DeleteMapping("/{id}/tracks/{trackId}")
    fun removeTrack(
        @PathVariable id: UUID,
        @PathVariable trackId: UUID,
        @AuthenticationPrincipal principal: UserPrincipal
    ): PlaylistResponse =
        playlistUseCase.removeTrack(id, trackId, principal.id)
}
