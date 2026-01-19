package ua.com.goit.clearbreath.analysis.domain.mapper

import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.HistoryEntity
import ua.com.goit.clearbreath.analysis.domain.models.HistoryItemResult
import ua.com.goit.clearbreath.analysis.domain.services.AdvisingService
import ua.com.goit.clearbreath.analysis.domain.services.SummaryService
import ua.com.goit.clearbreath.analysis.model.HistoryItem
import ua.com.goit.clearbreath.analysis.model.ModelDetail
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Component
class HistoryMapper(
    private val sourceTypeMapper: SourceTypeMapper,
    private val processingStatusMapper: ProcessingStatusMapper,
    private val summaryService: SummaryService,
    private val adviser: AdvisingService,
    private val probabilityStatusMapper: ProbabilityStatusMapper
) {
    suspend fun mapHistory(
        history: HistoryEntity,
        itemResults: List<HistoryItemResult>
    ): HistoryItem {
        return HistoryItem(
            history.requestId.toString(),
            OffsetDateTime.of(history.createdAt, ZoneOffset.UTC),
            sourceTypeMapper.toDto(history.sourceType),
            processingStatusMapper.toDto(history.processingStatus),
            summaryService.summarize(itemResults).map { getDetails(it.key, it.value) },
            history.recommendation,
        )
    }

    private fun getDetails(disease: String, probability: Double): ModelDetail {
        val advice = adviser.giveAdvise(probability)
        return ModelDetail(
            advice.advice,
            probabilityStatusMapper.toDto(advice.status),
            disease,
            probability.toFloat()
        )
    }
}