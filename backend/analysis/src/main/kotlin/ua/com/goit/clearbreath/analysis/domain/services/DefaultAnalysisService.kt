package ua.com.goit.clearbreath.analysis.domain.services

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import ua.com.goit.clearbreath.analysis.domain.models.HistoryEntity
import ua.com.goit.clearbreath.analysis.domain.models.ProcessingStatusEntity
import ua.com.goit.clearbreath.analysis.domain.models.SourceTypeEntity
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryRepository
import ua.com.goit.clearbreath.analysis.tasks.ConvertFileEvent
import ua.com.goit.clearbreath.analysis.tasks.EventProducer
import ua.com.goit.clearbreath.analysis.utils.DiskUtil
import java.nio.file.Path

@Service
class DefaultAnalysisService(
    private val repository: HistoryRepository,
    private val userService: UserService,
    private val eventProducer: EventProducer
) : AnalysisService {

    override suspend fun startAnalysis(
        fileDesc: AnalysisService.FileDesc,
        sourceType: SourceTypeEntity
    ): HistoryEntity {

        val userId = userService.getCurrentUser().userId
            ?: throw IllegalStateException("User ID is null")

        //TODO switch to outbox pattern to ensure event delivery
        val saved = repository.save(
            HistoryEntity(
                processingStatus = ProcessingStatusEntity.NEW,
                sourceType = sourceType,
                user = userId
            )
        ).awaitSingle()

        val requestId = saved.requestId ?: throw IllegalStateException("Request ID is null")

        try {

            val fileName = "$requestId.${fileDesc.extension}"

            val onLocalDisk: Path =
                DiskUtil.saveOriginalToTempDirectoryOnDisk(fileDesc.fileContent, fileName).awaitSingle()

            val updated = repository.save(
                saved.copy(
                    processingStatus = ProcessingStatusEntity.UPLOADED
                )
            ).awaitSingle()

            ConvertFileEvent(requestId, onLocalDisk, sourceType).let {
                eventProducer.publishEvent(it)
            }

            return updated;
        } catch (ex: Exception) {
            repository.save(
                saved.copy(
                    processingStatus = ProcessingStatusEntity.FAILED
                )
            ).awaitSingle()
            throw ex
        }
    }
}