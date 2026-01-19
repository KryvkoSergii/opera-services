package ua.com.goit.clearbreath.analysis.listeners

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.events.InferenceResultEventPayload
import io.awspring.cloud.sqs.annotation.SqsListener
import kotlinx.coroutines.reactor.awaitSingle
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.models.HistoryEntity
import ua.com.goit.clearbreath.analysis.domain.models.ProcessingStatusEntity
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryProcessingItemRepository
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryRepository
import ua.com.goit.clearbreath.analysis.events.EventStatus
import java.util.UUID

@Component
class ResultEventListener(
    private val itemsRepository: HistoryProcessingItemRepository,
    private val historyRepository: HistoryRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @SqsListener("\${sqs.inference-result-queue}")
    fun handle(message: InferenceResultEventPayload) {
        log.info(
            "Received result: requestId={}, itemId={}, model={}, status={}",
            message.requestId,
            message.itemId,
            message.modelName,
            message.status
        )

        process(message)
            .doOnError { e -> log.error("Failed to process result event: {}", message, e) }
            .subscribe()
    }

    private fun process(message: InferenceResultEventPayload): Mono<Void> {
        if (message.status != EventStatus.FAILED) {
            return Mono.empty()
        }

        val itemId = UUID.fromString(message.itemId)
        val requestId = UUID.fromString(message.requestId)

        // 1) mark item FAILED
        // 2) check if any item for request is NOT FAILED
        // 3) if none -> mark history FAILED
        return itemsRepository.findById(itemId)
            .switchIfEmpty(
                Mono.empty()
            )
            .flatMap { item ->
                itemsRepository.save(item.copy(processingStatus = ProcessingStatusEntity.FAILED))
            }
            .flatMap { _ ->
                itemsRepository.existsByRequestIdAndProcessingStatusNot(
                    requestId,
                    ProcessingStatusEntity.FAILED
                )
            }
            .flatMap { hasNotFailed ->
                if (hasNotFailed) {
                    Mono.empty()
                } else {
                    historyRepository.findById(requestId)
                        .flatMap { historyRepository.save(it.copy(processingStatus = ProcessingStatusEntity.FAILED)) }
                        .then()
                }
            }
            .then()
    }
}