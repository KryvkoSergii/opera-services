package ua.com.goit.clearbreath.analysis.eventhubs

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Component
class RequestEventsHub {

    private val sinks = ConcurrentHashMap<UUID, Sinks.Many<RequestStatusEvent>>()

    private fun sink(requestId: UUID): Sinks.Many<RequestStatusEvent> =
        sinks.computeIfAbsent(requestId) {
            Sinks.many().multicast().onBackpressureBuffer()
        }

    fun publish(event: RequestStatusEvent) {
        sink(event.requestId).tryEmitNext(event)
    }

    fun stream(requestId: UUID): Flux<RequestStatusEvent> = sink(requestId).asFlux()
}