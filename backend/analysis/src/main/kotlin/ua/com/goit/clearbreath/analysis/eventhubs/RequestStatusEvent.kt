package ua.com.goit.clearbreath.analysis.eventhubs

import ua.com.goit.clearbreath.analysis.domain.models.ProcessingStatusEntity
import java.time.Instant
import java.util.UUID

data class RequestStatusEvent(
    val requestId: UUID,
    val status: ProcessingStatusEntity,
    val message: String,
    val ts: Instant = Instant.now()
)