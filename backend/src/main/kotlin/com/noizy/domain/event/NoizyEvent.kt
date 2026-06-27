package com.noizy.domain.event

import java.time.Instant
import java.util.UUID

enum class NoizyEventType {
    TRACK_PLAYED,
    TRACK_UPLOADED,
    PLAYLIST_CREATED,
    USER_REGISTERED
}

data class NoizyEvent(
    val id: UUID = UUID.randomUUID(),
    val type: NoizyEventType,
    val version: Int = 1,
    val occurredAt: Instant = Instant.now(),
    val actorUserId: UUID? = null,
    val aggregateId: UUID? = null,
    val metadata: Map<String, String> = emptyMap()
)
