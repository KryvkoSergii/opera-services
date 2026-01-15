package ua.com.goit.clearbreath.analysis.domain.services

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.mapper.ProcessingStatusMapper
import ua.com.goit.clearbreath.analysis.domain.mapper.SourceTypeMapper
import ua.com.goit.clearbreath.analysis.domain.models.HistoryEntity
import ua.com.goit.clearbreath.analysis.domain.models.ProcessingStatusEntity
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryRepository
import ua.com.goit.clearbreath.analysis.domain.repositories.StorageRepository
import ua.com.goit.clearbreath.analysis.model.AnalysisCreateResponse
import ua.com.goit.clearbreath.analysis.model.SourceType
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class DefaultAnalysisService(
    private val repository: HistoryRepository,
    private val statusMapper: ProcessingStatusMapper,
    private val sourceTypeMapper: SourceTypeMapper,
    private val storageRepository: StorageRepository
) : AnalysisService {

    override suspend fun startAnalysis(
        fileDesc: AnalysisService.FileDesc,
        sourceType: SourceType
    ): AnalysisCreateResponse {

        val requestId = UUID.randomUUID()

        val request = HistoryEntity(
            requestId = requestId,
            processingStatus = ProcessingStatusEntity.NEW,
            sourceType = sourceTypeMapper.toEntity(sourceType),
            user = UUID.randomUUID()
        )

        repository.save(request)


        storageRepository.saveOriginalFile(requestId, fileDesc.extension, fileDesc.fileContent)
        //save to S3 original file
        //backet/original/request-id
        //preprocess file ????


        return repository.save(request)
            .map { i ->
                AnalysisCreateResponse(
                    i.requestId.toString(),
                    statusMapper.toDto(i.processingStatus),
                    OffsetDateTime.of(i.createdAt, ZoneOffset.UTC)
                )
            }.awaitSingle()
    }
}