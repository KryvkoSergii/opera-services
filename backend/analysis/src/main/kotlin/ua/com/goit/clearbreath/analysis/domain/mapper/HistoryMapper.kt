package ua.com.goit.clearbreath.analysis.domain.mapper

import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.HistoryEntity
import ua.com.goit.clearbreath.analysis.domain.models.HistoryItemResult
import ua.com.goit.clearbreath.analysis.domain.models.HistoryProcessingItem
import ua.com.goit.clearbreath.analysis.model.HistoryItem
import ua.com.goit.clearbreath.analysis.model.ModelDetail
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Component
class HistoryMapper(
    private val sourceTypeMapper: SourceTypeMapper,
    private val processingStatusMapper: ProcessingStatusMapper
) {
    fun mapHistory(
        history: HistoryEntity,
        processingItems: List<HistoryProcessingItem>,
        itemResults: List<HistoryItemResult>
    ): HistoryItem {

        val byItemId = itemResults.groupBy { it.itemId }

        val details = processingItems.map { processingItem -> ModelDetail() }

        return HistoryItem(
            history.requestId.toString(),
            OffsetDateTime.of(history.createdAt, ZoneOffset.UTC),
            sourceTypeMapper.toDto(history.sourceType),
            processingStatusMapper.toDto(history.processingStatus),
            details,
            history.recommendation,
        )
    }
}