package ua.com.goit.clearbreath.analysis.domain.services

import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.model.PaginatedRequestHistory

interface HistoryService {
    fun getHistory(page: Int, perPage: Int): Mono<PaginatedRequestHistory>
}