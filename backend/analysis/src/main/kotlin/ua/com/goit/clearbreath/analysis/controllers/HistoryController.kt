package ua.com.goit.clearbreath.analysis.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ua.com.goit.clearbreath.analysis.api.HistoryApi
import ua.com.goit.clearbreath.analysis.domain.services.HistoryService
import ua.com.goit.clearbreath.analysis.model.PaginatedRequestHistory

@RestController
class HistoryController(private val historyService: HistoryService) : HistoryApi {

    override suspend fun listAnalysisRequests(
        page: Int,
        perPage: Int,
        xTimezone: kotlin.String?
    ): ResponseEntity<PaginatedRequestHistory> {
        historyService.getHistory(page, perPage).let {
            return ResponseEntity.ok(it)
        }
    }
}