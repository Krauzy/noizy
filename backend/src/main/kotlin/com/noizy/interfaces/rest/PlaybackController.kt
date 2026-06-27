package com.noizy.interfaces.rest

import com.noizy.application.service.TrackService
import com.noizy.infrastructure.security.UserPrincipal
import com.noizy.interfaces.dto.PlaybackHistoryResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/playback")
class PlaybackController(
    private val trackService: TrackService
) {
    @PostMapping("/{trackId}")
    fun played(@PathVariable trackId: UUID, @AuthenticationPrincipal principal: UserPrincipal): PlaybackHistoryResponse =
        trackService.registerPlayback(trackId, principal.id)

    @GetMapping("/history")
    fun history(@AuthenticationPrincipal principal: UserPrincipal): List<PlaybackHistoryResponse> =
        trackService.playbackHistory(principal.id)
}
