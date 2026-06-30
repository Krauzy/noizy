package com.noizy.adapters.input.web

import com.noizy.adapters.output.security.UserPrincipal
import com.noizy.application.dto.PlaybackHistoryResponse
import com.noizy.application.port.input.TrackUseCase
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
    private val trackUseCase: TrackUseCase
) {
    @PostMapping("/{trackId}")
    fun played(@PathVariable trackId: UUID, @AuthenticationPrincipal principal: UserPrincipal): PlaybackHistoryResponse =
        trackUseCase.registerPlayback(trackId, principal.id)

    @GetMapping("/history")
    fun history(@AuthenticationPrincipal principal: UserPrincipal): List<PlaybackHistoryResponse> =
        trackUseCase.playbackHistory(principal.id)
}
