package com.noizy.application.service

import com.noizy.domain.event.NoizyEvent
import com.noizy.domain.event.NoizyEventType
import com.noizy.domain.exception.BadRequestException
import com.noizy.domain.exception.NotFoundException
import com.noizy.infrastructure.aws.S3StorageService
import com.noizy.infrastructure.messaging.EventPublisher
import com.noizy.infrastructure.persistence.entity.LikedTrackEntity
import com.noizy.infrastructure.persistence.entity.PlaybackHistoryEntity
import com.noizy.infrastructure.persistence.entity.TrackEntity
import com.noizy.infrastructure.persistence.repository.LikedTrackJpaRepository
import com.noizy.infrastructure.persistence.repository.PlaybackHistoryJpaRepository
import com.noizy.infrastructure.persistence.repository.TrackJpaRepository
import com.noizy.infrastructure.persistence.repository.UserJpaRepository
import com.noizy.interfaces.dto.PlaybackHistoryResponse
import com.noizy.interfaces.dto.TrackRequest
import com.noizy.interfaces.dto.TrackResponse
import com.noizy.interfaces.dto.TrackStreamResult
import com.noizy.interfaces.dto.TrackUpdateRequest
import com.noizy.interfaces.dto.TrackUploadResponse
import com.noizy.interfaces.mapper.toResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class TrackService(
    private val tracks: TrackJpaRepository,
    private val users: UserJpaRepository,
    private val likes: LikedTrackJpaRepository,
    private val history: PlaybackHistoryJpaRepository,
    private val artistService: ArtistService,
    private val albumService: AlbumService,
    private val storage: S3StorageService,
    private val eventPublisher: EventPublisher
) {
    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<TrackResponse> =
        tracks.findAll(pageable).map { it.toResponse() }

    @Transactional(readOnly = true)
    fun search(query: String, pageable: Pageable): Page<TrackResponse> =
        tracks.search(query.trim(), pageable).map { it.toResponse() }

    @Transactional(readOnly = true)
    fun get(id: UUID): TrackResponse = getEntity(id).toResponse()

    @Transactional
    fun create(request: TrackRequest): TrackResponse {
        val track = tracks.save(
            TrackEntity(
                title = request.title.trim(),
                artist = artistService.getEntity(request.artistId),
                album = request.albumId?.let { albumService.getEntity(it) },
                genre = request.genre?.trim(),
                durationSeconds = request.durationSeconds,
                audioS3Key = request.audioS3Key.trim(),
                coverS3Key = request.coverS3Key?.trim()
            )
        )
        return track.toResponse()
    }

    @Transactional
    fun upload(
        title: String,
        artistId: UUID,
        albumId: UUID?,
        genre: String?,
        durationSeconds: Int,
        audio: MultipartFile,
        cover: MultipartFile?,
        userId: UUID
    ): TrackUploadResponse {
        if (audio.isEmpty) throw BadRequestException("Audio file is required")
        val audioKey = storage.uploadAudio(audio)
        val coverKey = cover?.takeIf { !it.isEmpty }?.let { storage.uploadCover(it) }
        val track = tracks.save(
            TrackEntity(
                title = title.trim(),
                artist = artistService.getEntity(artistId),
                album = albumId?.let { albumService.getEntity(it) },
                genre = genre?.trim(),
                durationSeconds = durationSeconds.coerceAtLeast(0),
                audioS3Key = audioKey,
                coverS3Key = coverKey
            )
        )
        eventPublisher.publish(
            NoizyEvent(
                type = NoizyEventType.TRACK_UPLOADED,
                actorUserId = userId,
                aggregateId = track.id,
                metadata = mapOf("title" to track.title)
            )
        )
        return TrackUploadResponse(track.toResponse(), audioKey, coverKey)
    }

    @Transactional
    fun update(id: UUID, request: TrackUpdateRequest): TrackResponse {
        val track = getEntity(id)
        track.title = request.title.trim()
        track.album = request.albumId?.let { albumService.getEntity(it) }
        track.genre = request.genre?.trim()
        track.durationSeconds = request.durationSeconds.coerceAtLeast(0)
        track.coverS3Key = request.coverS3Key?.trim()
        return track.toResponse()
    }

    @Transactional
    fun delete(id: UUID) {
        if (!tracks.existsById(id)) throw NotFoundException("Track")
        tracks.deleteById(id)
    }

    @Transactional
    fun stream(id: UUID, rangeHeader: String?, userId: UUID?): TrackStreamResult {
        val track = getEntity(id)
        track.playCount += 1
        userId?.let { registerPlaybackInternal(track, it) }
        eventPublisher.publish(
            NoizyEvent(
                type = NoizyEventType.TRACK_PLAYED,
                actorUserId = userId,
                aggregateId = track.id,
                metadata = mapOf("title" to track.title)
            )
        )
        val s3Stream = storage.getAudio(track.audioS3Key, rangeHeader)
        val contentRange = buildContentRange(rangeHeader, s3Stream.totalLength, s3Stream.returnedLength)
        return TrackStreamResult(
            content = s3Stream.stream,
            contentType = s3Stream.contentType,
            contentLength = s3Stream.returnedLength,
            statusCode = if (contentRange == null) 200 else 206,
            contentRange = contentRange
        )
    }

    @Transactional
    fun registerPlayback(trackId: UUID, userId: UUID): PlaybackHistoryResponse {
        val track = getEntity(trackId)
        track.playCount += 1
        val entry = registerPlaybackInternal(track, userId)
        eventPublisher.publish(
            NoizyEvent(
                type = NoizyEventType.TRACK_PLAYED,
                actorUserId = userId,
                aggregateId = track.id
            )
        )
        return entry.toResponse()
    }

    @Transactional(readOnly = true)
    fun playbackHistory(userId: UUID): List<PlaybackHistoryResponse> =
        history.findTop50ByUserIdOrderByPlayedAtDesc(userId).map { it.toResponse() }

    @Transactional
    fun like(trackId: UUID, userId: UUID): TrackResponse {
        val track = getEntity(trackId)
        if (!likes.existsByUserIdAndTrackId(userId, trackId)) {
            val user = users.findById(userId).orElseThrow { NotFoundException("User") }
            likes.save(LikedTrackEntity(user = user, track = track))
        }
        return track.toResponse()
    }

    @Transactional
    fun unlike(trackId: UUID, userId: UUID) {
        likes.deleteByUserIdAndTrackId(userId, trackId)
    }

    @Transactional(readOnly = true)
    fun liked(userId: UUID): List<TrackResponse> =
        likes.findByUserIdOrderByCreatedAtDesc(userId).map { it.track.toResponse() }

    fun getEntity(id: UUID): TrackEntity =
        tracks.findById(id).orElseThrow { NotFoundException("Track") }

    private fun registerPlaybackInternal(track: TrackEntity, userId: UUID): PlaybackHistoryEntity {
        val user = users.findById(userId).orElseThrow { NotFoundException("User") }
        return history.save(PlaybackHistoryEntity(user = user, track = track))
    }

    private fun buildContentRange(rangeHeader: String?, totalLength: Long, returnedLength: Long): String? {
        if (rangeHeader.isNullOrBlank() || !rangeHeader.startsWith("bytes=")) return null
        val range = rangeHeader.removePrefix("bytes=").substringBefore(',')
        val startText = range.substringBefore('-')
        val endText = range.substringAfter('-', "")
        val start = startText.toLongOrNull() ?: return null
        val end = endText.toLongOrNull() ?: (start + returnedLength - 1).coerceAtMost(totalLength - 1)
        return "bytes $start-$end/$totalLength"
    }
}
