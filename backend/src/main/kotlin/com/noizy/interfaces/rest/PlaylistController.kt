package com.noizy.interfaces.rest

import com.noizy.application.service.PlaylistService
import com.noizy.infrastructure.security.UserPrincipal
import com.noizy.interfaces.dto.PlaylistRequest
import com.noizy.interfaces.dto.PlaylistResponse
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
    private val playlistService: PlaylistService
) {
    @PostMapping
    fun create(@Valid @RequestBody request: PlaylistRequest, @AuthenticationPrincipal principal: UserPrincipal): PlaylistResponse =
        playlistService.create(request, principal.id)

    @GetMapping("/me")
    fun mine(@AuthenticationPrincipal principal: UserPrincipal): List<PlaylistResponse> =
        playlistService.mine(principal.id)

    @GetMapping("/public")
    fun publicPlaylists(): List<PlaylistResponse> =
        playlistService.publicPlaylists()

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID, @AuthenticationPrincipal principal: UserPrincipal?): PlaylistResponse =
        playlistService.get(id, principal?.id)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: PlaylistRequest,
        @AuthenticationPrincipal principal: UserPrincipal
    ): PlaylistResponse =
        playlistService.update(id, request, principal.id)

    @PostMapping("/{id}/tracks/{trackId}")
    fun addTrack(
        @PathVariable id: UUID,
        @PathVariable trackId: UUID,
        @AuthenticationPrincipal principal: UserPrincipal
    ): PlaylistResponse =
        playlistService.addTrack(id, trackId, principal.id)

    @DeleteMapping("/{id}/tracks/{trackId}")
    fun removeTrack(
        @PathVariable id: UUID,
        @PathVariable trackId: UUID,
        @AuthenticationPrincipal principal: UserPrincipal
    ): PlaylistResponse =
        playlistService.removeTrack(id, trackId, principal.id)
}
