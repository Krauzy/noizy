package com.noizy.application.port.output

import com.noizy.domain.event.NoizyEvent

interface DomainEventPublisher {
    fun publish(event: NoizyEvent)
}
