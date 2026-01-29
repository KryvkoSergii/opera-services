package ua.com.goit.clearbreath.analysis.controllers

import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import ua.com.goit.clearbreath.analysis.api.RealtimeApi.Companion.PATH_STREAM_REQUEST_EVENTS
import ua.com.goit.clearbreath.analysis.domain.mapper.ProcessingStatusMapper
import ua.com.goit.clearbreath.analysis.eventhubs.RequestEventsHub
import ua.com.goit.clearbreath.analysis.model.StatusEvent
import ua.com.goit.clearbreath.analysis.utils.TimeZoneUtils
import java.time.Duration
import java.time.OffsetDateTime
import java.util.*

@RestController
class StatusController(private val hub: RequestEventsHub, private val mapper: ProcessingStatusMapper) {

    @RequestMapping(
        method = [RequestMethod.GET],
        value = [PATH_STREAM_REQUEST_EVENTS],
        produces = ["text/event-stream"]
    )
    suspend fun streamRequestEvents(
        @PathVariable("requestId") requestId: String,
        @RequestHeader(value = "X-Timezone", required = false) xTimezone: String?
    ): Flux<ServerSentEvent<StatusEvent>> {
        val zoneId = TimeZoneUtils.resolveZoneId(xTimezone)

        val dataStream = hub.stream(UUID.fromString(requestId))
            .map { ev ->
                StatusEvent(
                    ev.requestId,
                    mapper.toDto(ev.status),
                    OffsetDateTime.ofInstant(ev.ts, zoneId)
                )
            }
            .map { ev ->
                ServerSentEvent.builder(ev)
                    .event("status")
                    .id(UUID.randomUUID().toString())
                    .build()
            }

        val heartbeat = Flux.interval(Duration.ofSeconds(15))
            .map {
                ServerSentEvent.builder<StatusEvent>()
                    .event("ping")
                    .comment("keep-alive")
                    .build()
            }

        return Flux.merge(heartbeat, dataStream)
    }
}