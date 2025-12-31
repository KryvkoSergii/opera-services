package ua.com.goit.clearbreath.domain.services

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ua.com.goit.clearbreath.domain.models.HistoryEntity
import ua.com.goit.clearbreath.domain.models.ProcessingStatusEntity
import ua.com.goit.clearbreath.domain.models.SourceTypeEntity
import ua.com.goit.clearbreath.domain.repositories.HistoryRepository
import ua.com.goit.clearbreath.model.AnalysisCreateResponse
import ua.com.goit.clearbreath.model.RequestStatus
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class DefaultAnalysisService(private val repository: HistoryRepository) : AnalysisService {

    override suspend fun startAnalysis(
        file: MultipartFile,
        sourceType: String
    ): AnalysisCreateResponse {
        val request = HistoryEntity(
            processingStatus = ProcessingStatusEntity.IN_PROGRESS,
            sourceType = SourceTypeEntity.MICROPHONE,
            user = UUID.randomUUID()
        )
            .also(repository::save)

        return AnalysisCreateResponse(
            request.id.toString(),
            mapProcessingStatus(request.processingStatus),
            OffsetDateTime.of(request.createdAt, ZoneOffset.UTC)
        )
    }

    fun mapProcessingStatus(processingStatus: ProcessingStatusEntity): RequestStatus {
        return when (processingStatus) {
            ProcessingStatusEntity.IN_PROGRESS -> RequestStatus.IN_PROGRESS
            ProcessingStatusEntity.FINISHED -> RequestStatus.FINISHED
            ProcessingStatusEntity.FAILED -> RequestStatus.FAILED
            else -> throw IllegalArgumentException("Unsupported source type: $processingStatus")
        }
    }
}