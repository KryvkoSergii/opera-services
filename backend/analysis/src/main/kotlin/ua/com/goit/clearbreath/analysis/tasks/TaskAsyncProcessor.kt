package ua.com.goit.clearbreath.analysis.tasks

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.mapper.SourceTypeMapper
import ua.com.goit.clearbreath.analysis.domain.models.HistoryEntity
import ua.com.goit.clearbreath.analysis.domain.models.HistoryProcessingItem
import ua.com.goit.clearbreath.analysis.domain.models.ProcessingStatusEntity
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryProcessingItemRepository
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryRepository
import ua.com.goit.clearbreath.analysis.events.InferenceStartEventPayload
import ua.com.goit.clearbreath.analysis.infra.QueueEventPublisher
import ua.com.goit.clearbreath.analysis.infra.StorageRepository
import ua.com.goit.clearbreath.analysis.utils.DiskUtil
import ua.com.goit.clearbreath.analysis.utils.FileConvertingUtil

@Component
class TaskAsyncProcessor(
    private val itemRepository: HistoryProcessingItemRepository,
    private val historyRepository: HistoryRepository,
    private val storageRepository: StorageRepository,
    private val publisher: QueueEventPublisher,
    private val sourceMapper: SourceTypeMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Async("taskExecutor")
    @EventListener
    fun on(event: ConvertFileEvent) {
        val requestId = event.requestId
        val outputFilePattern = "${DiskUtil.CONVERTED_DIRECTORY}/${requestId}_%03d.wav"

        try {
            FileConvertingUtil.runFfmpeg(event.onLocalDisk.toString(), outputFilePattern)
        } catch (e: Exception) {
            historyRepository.save(
                HistoryEntity(
                    requestId = requestId,
                    processingStatus = ProcessingStatusEntity.FAILED
                )
            ).subscribe()
            log.error("File converting failed requestId={}", requestId, e)
            return
        }

        Flux.fromIterable(FileConvertingUtil.listConvertedFiles(requestId))
            .concatMap { localPath ->
                storageRepository.saveConvertedFileToRemoteStorage(localPath)
                    .flatMap { remoteRef ->
                        itemRepository.save(
                            HistoryProcessingItem(
                                requestId = requestId,
                                fileLocation = remoteRef.toString(),
                                processingStatus = ProcessingStatusEntity.UPLOADED
                            )
                        )
                    }
                    .onErrorResume { ex ->
                        // upload failed -> mark FAILED
                        itemRepository.save(
                            HistoryProcessingItem(
                                requestId = requestId,
                                fileLocation = localPath.toString(),
                                processingStatus = ProcessingStatusEntity.FAILED
                            )
                        )
                            .doOnNext {
                                log.debug("S3 upload failed. Marked FAILED. file={} reason={}", localPath, ex.message)
                            }
                            .then(Mono.empty())
                    }
                    .flatMap { item ->
                        // publish to SQS
                        publisher.publishStartInference(
                            InferenceStartEventPayload(
                                requestId = item.requestId.toString(),
                                itemId = item.itemId.toString(),
                                fileLocation = item.fileLocation.toString(),
                                sourceMapper.toEvent(event.source)
                            )
                        )
                            .flatMap { resp ->
                                // publish succeeded -> mark PROCESSING
                                log.debug("SQS published. messageId={} itemId={}", resp.messageId(), item.itemId)
                                DiskUtil.removeTempFile(localPath)
                                    .then(
                                        itemRepository.save(item.copy(processingStatus = ProcessingStatusEntity.PROCESSING))
                                    )
                            }
                            .onErrorResume { ex ->
                                // publish failed -> mark FAILED
                                itemRepository.save(item.copy(processingStatus = ProcessingStatusEntity.FAILED))
                                    .doOnNext {
                                        log.error(
                                            "SQS publish failed -> FAILED. itemId={}. reason={}",
                                            item.itemId,
                                            ex.message,
                                            ex
                                        )
                                    }
                                    .then(Mono.empty())
                            }
                    }
            }
            .subscribe()
    }

}