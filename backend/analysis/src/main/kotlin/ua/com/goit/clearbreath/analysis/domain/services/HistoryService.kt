package ua.com.goit.clearbreath.analysis.domain.services

import ua.com.goit.clearbreath.analysis.model.PaginatedRequestHistory

interface HistoryService {
    suspend fun getHistory(page: Int, perPage: Int): PaginatedRequestHistory
}