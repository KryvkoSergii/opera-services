package ua.com.goit.clearbreath.analysis.domain.services

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryRepository
import ua.com.goit.clearbreath.analysis.model.PaginatedRequestHistory

@Service
class DefaultHistoryService(private val repository: HistoryRepository): HistoryService {

    override fun getHistory(page: Int, perPage: Int): Mono<PaginatedRequestHistory> {
        val pagination = PageRequest.of(page, perPage)
//        var found = repository.findPage(pagination, )
        return Mono.empty()
    }
}
