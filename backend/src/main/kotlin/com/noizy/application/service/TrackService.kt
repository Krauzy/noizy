package com.noizy.application.service

import com.noizy.domain.event.NoizyEventFactory
import com.noizy.domain.exception.BadRequestException
import com.noizy.domain.exception.NotFoundException
import com.noizy.domain.model.LikedTrackEntity
import com.noizy.domain.model.PlaybackHistoryEntity
import com.noizy.domain.model.TrackEntity
import com.noizy.application.dto.PlaybackHistoryResponse
import com.noizy.application.dto.TrackRequest
import com.noizy.application.dto.TrackResponse
import com.noizy.application.dto.TrackCoverResult
import com.noizy.application.dto.TrackStreamResult
import com.noizy.application.dto.TrackUpdateRequest
import com.noizy.application.dto.TrackUploadResponse
import com.noizy.application.mapper.toResponse
import com.noizy.application.port.input.TrackUseCase
import com.noizy.application.port.output.DomainEventPublisher
import com.noizy.application.port.output.TrackStoragePort
import com.noizy.application.port.output.persistence.LikedTrackRepositoryPort
import com.noizy.application.port.output.persistence.PlaybackHistoryRepositoryPort
import com.noizy.application.port.output.persistence.TrackRepositoryPort
import com.noizy.application.port.output.persistence.UserRepositoryPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class TrackService(
    private val tracks: TrackRepositoryPort,
    private val users: UserRepositoryPort,
    private val likes: LikedTrackRepositoryPort,
    private val history: PlaybackHistoryRepositoryPort,
    private val artistService: ArtistService,
    private val albumService: AlbumService,
    private val audioMetadata: AudioMetadataService,
    private val storage: TrackStoragePort,
    private val eventPublisher: DomainEventPublisher
) : TrackUseCase {
    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): Page<TrackResponse> =
        tracks.findAll(pageable).map { it.toResponse() }

    @Transactional(readOnly = true)
    override fun search(query: String, pageable: Pageable): Page<TrackResponse> =
        tracks.search(query.trim(), pageable).map { it.toResponse() }

    @Transactional(readOnly = true)
    override fun get(id: UUID): TrackResponse = getEntity(id).toResponse()

    @Transactional
    override fun create(request: TrackRequest): TrackResponse {
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
    override fun upload(
        title: String,
        albumId: UUID?,
        genre: String?,
        audio: MultipartFile,
        cover: MultipartFile?,
        userId: UUID
    ): TrackUploadResponse {
        if (audio.isEmpty) throw BadRequestException("Audio file is required")
        val user = users.findById(userId).orElseThrow { NotFoundException("User") }
        val artist = artistService.getOrCreateForUploader(user)
        val durationSeconds = audioMetadata.durationSeconds(audio)
        val audioKey = storage.uploadAudio(audio)
        val coverKey = cover?.takeIf { !it.isEmpty }?.let { storage.uploadCover(it) }
        val track = tracks.save(
            TrackEntity(
                title = title.trim(),
                artist = artist,
                album = albumId?.let { albumService.getEntity(it) },
                genre = genre?.trim(),
                durationSeconds = durationSeconds,
                audioS3Key = audioKey,
                coverS3Key = coverKey
            )
        )
        eventPublisher.publish(NoizyEventFactory.trackUploaded(userId, track.id, track.title))
        return TrackUploadResponse(track.toResponse(), audioKey, coverKey)
    }

    @Transactional
    override fun update(id: UUID, request: TrackUpdateRequest): TrackResponse {
        val track = getEntity(id)
        track.title = request.title.trim()
        track.album = request.albumId?.let { albumService.getEntity(it) }
        track.genre = request.genre?.trim()
        track.durationSeconds = request.durationSeconds.coerceAtLeast(0)
        track.coverS3Key = request.coverS3Key?.trim()
        return track.toResponse()
    }

    @Transactional
    override fun delete(id: UUID) {
        if (!tracks.existsById(id)) throw NotFoundException("Track")
        tracks.deleteById(id)
    }

    @Transactional
    override fun stream(id: UUID, rangeHeader: String?, userId: UUID?): TrackStreamResult {
        val track = getEntity(id)
        track.playCount += 1
        userId?.let { registerPlaybackInternal(track, it) }
        eventPublisher.publish(NoizyEventFactory.trackPlayed(userId, track.id, track.title))
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

    @Transactional(readOnly = true)
    override fun cover(id: UUID): TrackCoverResult {
        val track = getEntity(id)
        val key = track.coverS3Key ?: throw NotFoundException("Track cover")
        val image = storage.getImage(key)
        return TrackCoverResult(
            content = image.stream,
            contentType = image.contentType,
            contentLength = image.contentLength
        )
    }

    @Transactional
    override fun registerPlayback(trackId: UUID, userId: UUID): PlaybackHistoryResponse {
        val track = getEntity(trackId)
        track.playCount += 1
        val entry = registerPlaybackInternal(track, userId)
        eventPublisher.publish(NoizyEventFactory.trackPlayed(userId, track.id))
        return entry.toResponse()
    }

    @Transactional(readOnly = true)
    override fun playbackHistory(userId: UUID): List<PlaybackHistoryResponse> =
        history.findTop50ByUserIdOrderByPlayedAtDesc(userId).map { it.toResponse() }

    @Transactional
    override fun like(trackId: UUID, userId: UUID): TrackResponse {
        val track = getEntity(trackId)
        if (!likes.existsByUserIdAndTrackId(userId, trackId)) {
            val user = users.findById(userId).orElseThrow { NotFoundException("User") }
            likes.save(LikedTrackEntity(user = user, track = track))
        }
        return track.toResponse()
    }

    @Transactional
    override fun unlike(trackId: UUID, userId: UUID) {
        likes.deleteByUserIdAndTrackId(userId, trackId)
    }

    @Transactional(readOnly = true)
    override fun liked(userId: UUID): List<TrackResponse> =
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
