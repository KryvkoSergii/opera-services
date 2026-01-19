package ua.com.goit.clearbreath.analysis.listeners

import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.models.HistoryItemResult
import ua.com.goit.clearbreath.analysis.domain.models.ProcessingStatusEntity
import ua.com.goit.clearbreath.analysis.domain.repositories.EvaluationModelMetadataRepository
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryItemResultRepository
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryProcessingItemRepository
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryRepository
import ua.com.goit.clearbreath.analysis.events.EventStatus
import ua.com.goit.clearbreath.analysis.events.InferenceResultEventPayload
import java.util.*

@Component
class ResultEventListener(
    private val itemsRepository: HistoryProcessingItemRepository,
    private val historyRepository: HistoryRepository,
    private val resultRepository: HistoryItemResultRepository,
    private val modelRepository: EvaluationModelMetadataRepository
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
        val itemId = UUID.fromString(message.itemId)
        val requestId = UUID.fromString(message.requestId)

        if (message.status == EventStatus.COMPLETED) {
            // 1) save result
            val model = message.modelName
                ?: return Mono.error(IllegalArgumentException("Model name is null for COMPLETED event"))
            val saveResult = modelRepository.findByModelNameIn(setOf(model))
                .single()
                .flatMap { meta ->
                    resultRepository.save(
                        HistoryItemResult(
                            itemId = itemId,
                            requestId = requestId,
                            modelName = model,
                            diagnose = meta.diagnose,
                            probability = message.probability
                                ?: return@flatMap Mono.error(IllegalArgumentException("Probability is null for COMPLETED event"))
                        )
                    )
                }
                .then()

            val markDone = itemsRepository.findById(itemId)
                .switchIfEmpty(Mono.error(IllegalStateException("Item not found: $itemId")))
                .flatMap { item -> itemsRepository.save(item.copy(processingStatus = ProcessingStatusEntity.DONE)) }
                .flatMap {
                    itemsRepository.existsByRequestIdAndProcessingStatusNot(requestId, ProcessingStatusEntity.DONE)
                }
                .flatMap { hasNotDone ->
                    if (hasNotDone) Mono.empty()
                    else historyRepository.findById(requestId)
                        .flatMap { historyRepository.save(it.copy(processingStatus = ProcessingStatusEntity.DONE)) }
                        .then()
                }
                .then()

            return saveResult.then(markDone)
        }

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