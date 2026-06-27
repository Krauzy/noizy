package com.noizy.interfaces.mapper

import com.noizy.domain.exception.NotFoundException
import com.noizy.infrastructure.persistence.entity.AlbumEntity
import com.noizy.infrastructure.persistence.entity.ArtistEntity
import com.noizy.infrastructure.persistence.entity.PlaybackHistoryEntity
import com.noizy.infrastructure.persistence.entity.PlaylistEntity
import com.noizy.infrastructure.persistence.entity.PlaylistTrackEntity
import com.noizy.infrastructure.persistence.entity.TrackEntity
import com.noizy.infrastructure.persistence.entity.UserEntity
import com.noizy.interfaces.dto.AlbumResponse
import com.noizy.interfaces.dto.ArtistResponse
import com.noizy.interfaces.dto.PlaybackHistoryResponse
import com.noizy.interfaces.dto.PlaylistResponse
import com.noizy.interfaces.dto.TrackResponse
import com.noizy.interfaces.dto.UserResponse

fun UserEntity.toResponse() = UserResponse(
    id = id ?: throw NotFoundException("User id"),
    name = name,
    email = email,
    role = role,
    createdAt = createdAt
)

fun ArtistEntity.toResponse() = ArtistResponse(
    id = id ?: throw NotFoundException("Artist id"),
    name = name,
    description = description,
    imageUrl = imageUrl,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun AlbumEntity.toResponse() = AlbumResponse(
    id = id ?: throw NotFoundException("Album id"),
    title = title,
    artist = artist.toResponse(),
    coverUrl = coverUrl,
    releaseDate = releaseDate,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun TrackEntity.toResponse() = TrackResponse(
    id = id ?: throw NotFoundException("Track id"),
    title = title,
    artist = artist.toResponse(),
    album = album?.toResponse(),
    genre = genre,
    durationSeconds = durationSeconds,
    audioS3Key = audioS3Key,
    coverS3Key = coverS3Key,
    playCount = playCount,
    streamUrl = "/api/tracks/${id}/stream",
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PlaylistEntity.toResponse(tracks: List<PlaylistTrackEntity>) = PlaylistResponse(
    id = id ?: throw NotFoundException("Playlist id"),
    name = name,
    description = description,
    owner = owner.toResponse(),
    isPublic = isPublic,
    tracks = tracks.sortedBy { it.position }.map { it.track.toResponse() },
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PlaybackHistoryEntity.toResponse() = PlaybackHistoryResponse(
    id = id ?: throw NotFoundException("Playback history id"),
    track = track.toResponse(),
    playedAt = playedAt
)
