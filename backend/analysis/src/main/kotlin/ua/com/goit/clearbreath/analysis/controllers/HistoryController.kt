package ua.com.goit.clearbreath.analysis.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ua.com.goit.clearbreath.analysis.api.HistoryApi
import ua.com.goit.clearbreath.analysis.model.PaginatedRequestHistory

@RestController
class HistoryController : HistoryApi {

    override fun listAnalysisRequests(
        page: Int,
        perPage: Int
    ): ResponseEntity<PaginatedRequestHistory> {


        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }
}