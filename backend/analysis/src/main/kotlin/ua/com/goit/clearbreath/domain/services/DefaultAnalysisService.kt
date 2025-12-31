package ua.com.goit.clearbreath.domain.services

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.domain.mapper.ProcessingStatusMapper
import ua.com.goit.clearbreath.domain.mapper.SourceTypeMapper
import ua.com.goit.clearbreath.domain.models.HistoryEntity
import ua.com.goit.clearbreath.domain.models.ProcessingStatusEntity
import ua.com.goit.clearbreath.domain.repositories.HistoryRepository
import ua.com.goit.clearbreath.model.AnalysisCreateResponse
import ua.com.goit.clearbreath.model.SourceType
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class DefaultAnalysisService(
    private val repository: HistoryRepository,
    private val statusMapper: ProcessingStatusMapper,
    private val sourceTypeMapper: SourceTypeMapper
) : AnalysisService {

    override fun startAnalysis(
        file: MultipartFile,
        sourceType: SourceType
    ): Mono<AnalysisCreateResponse> {
        val request = HistoryEntity(
            processingStatus = ProcessingStatusEntity.IN_PROGRESS,
            sourceType = sourceTypeMapper.toEntity(sourceType),
            user = UUID.randomUUID()
        )

        return repository.save(request)
            .map { i ->
                AnalysisCreateResponse(
                    i.toString(),
                    statusMapper.toDto(i.processingStatus),
                    OffsetDateTime.of(i.createdAt, ZoneOffset.UTC)
                )
            }
    }
}