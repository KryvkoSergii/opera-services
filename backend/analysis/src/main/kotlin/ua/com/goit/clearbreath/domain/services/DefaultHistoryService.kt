package ua.com.goit.clearbreath.domain.services

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.domain.repositories.HistoryRepository
import ua.com.goit.clearbreath.model.PaginatedRequestHistory

@Service
class DefaultHistoryService(private val repository: HistoryRepository): HistoryService {

    override fun getHistory(page: Int, perPage: Int): Mono<PaginatedRequestHistory> {
       var found = repository.findPage(PageRequest.of(page, perPage), )

    }
}
