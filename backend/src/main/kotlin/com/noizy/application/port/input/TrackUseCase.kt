package com.noizy.application.port.input

import com.noizy.application.dto.PlaybackHistoryResponse
import com.noizy.application.dto.TrackCoverResult
import com.noizy.application.dto.TrackRequest
import com.noizy.application.dto.TrackResponse
import com.noizy.application.dto.TrackStreamResult
import com.noizy.application.dto.TrackUpdateRequest
import com.noizy.application.dto.TrackUploadResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

interface TrackUseCase {
    fun list(pageable: Pageable): Page<TrackResponse>
    fun search(query: String, pageable: Pageable): Page<TrackResponse>
    fun get(id: UUID): TrackResponse
    fun create(request: TrackRequest): TrackResponse
    fun upload(
        title: String,
        albumId: UUID?,
        genre: String?,
        audio: MultipartFile,
        cover: MultipartFile?,
        userId: UUID
    ): TrackUploadResponse
    fun update(id: UUID, request: TrackUpdateRequest): TrackResponse
    fun delete(id: UUID)
    fun stream(id: UUID, rangeHeader: String?, userId: UUID?): TrackStreamResult
    fun cover(id: UUID): TrackCoverResult
    fun registerPlayback(trackId: UUID, userId: UUID): PlaybackHistoryResponse
    fun playbackHistory(userId: UUID): List<PlaybackHistoryResponse>
    fun like(trackId: UUID, userId: UUID): TrackResponse
    fun unlike(trackId: UUID, userId: UUID)
    fun liked(userId: UUID): List<TrackResponse>
}
