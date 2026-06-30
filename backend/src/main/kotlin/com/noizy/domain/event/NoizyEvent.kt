package com.noizy.domain.event

import java.time.Instant
import java.util.UUID

data class NoizyEvent(
    val id: UUID = UUID.randomUUID(),
    val type: NoizyEventType,
    val version: Int = 1,
    val occurredAt: Instant = Instant.now(),
    val actorUserId: UUID? = null,
    val aggregateId: UUID? = null,
    val metadata: Map<String, String> = emptyMap()
)
