package com.noizy.domain.event

import java.util.UUID

object NoizyEventFactory {
    fun userRegistered(userId: UUID?) = NoizyEvent(
        type = NoizyEventType.USER_REGISTERED,
        actorUserId = userId,
        aggregateId = userId
    )

    fun playlistCreated(ownerId: UUID, playlistId: UUID?) = NoizyEvent(
        type = NoizyEventType.PLAYLIST_CREATED,
        actorUserId = ownerId,
        aggregateId = playlistId
    )

    fun trackUploaded(userId: UUID, trackId: UUID?, title: String) = NoizyEvent(
        type = NoizyEventType.TRACK_UPLOADED,
        actorUserId = userId,
        aggregateId = trackId,
        metadata = mapOf("title" to title)
    )

    fun trackPlayed(userId: UUID?, trackId: UUID?, title: String? = null) = NoizyEvent(
        type = NoizyEventType.TRACK_PLAYED,
        actorUserId = userId,
        aggregateId = trackId,
        metadata = title?.let { mapOf("title" to it) } ?: emptyMap()
    )
}
