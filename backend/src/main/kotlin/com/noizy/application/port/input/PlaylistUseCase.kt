package com.noizy.application.port.input

import com.noizy.application.dto.PlaylistRequest
import com.noizy.application.dto.PlaylistResponse
import java.util.UUID

interface PlaylistUseCase {
    fun create(request: PlaylistRequest, ownerId: UUID): PlaylistResponse
    fun mine(ownerId: UUID): List<PlaylistResponse>
    fun publicPlaylists(): List<PlaylistResponse>
    fun get(id: UUID, requesterId: UUID?): PlaylistResponse
    fun update(id: UUID, request: PlaylistRequest, requesterId: UUID): PlaylistResponse
    fun addTrack(id: UUID, trackId: UUID, requesterId: UUID): PlaylistResponse
    fun removeTrack(id: UUID, trackId: UUID, requesterId: UUID): PlaylistResponse
}
