package ua.com.goit.clearbreath.analysis.domain.services

import ua.com.goit.clearbreath.analysis.domain.models.HistoryItemResult

interface SummaryService {
    suspend fun summarize(results: List<HistoryItemResult>): Map<String, Double>
}