package ua.com.goit.clearbreath.analysis.domain.services

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import ua.com.goit.clearbreath.analysis.domain.mapper.HistoryMapper
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryItemResultRepository
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryRepository
import ua.com.goit.clearbreath.analysis.model.PaginatedRequestHistory
import java.util.*

@Service
class DefaultHistoryService(
    private val historyRepository: HistoryRepository,
    private val userService: UserService,
    private val resultItemRepository: HistoryItemResultRepository,
    private val historyMapper: HistoryMapper
) : HistoryService {

    override suspend fun getHistory(page: Int, perPage: Int): PaginatedRequestHistory {
        val pagination = PageRequest.of(page, perPage)
        val userId: UUID = userService.getCurrentUser().userId ?: throw IllegalStateException("User ID is null")

        val historyList = historyRepository
            .findAllByUserOrderByCreatedAtDesc(userId, pagination)
            .collectList()
            .awaitSingle()

        val requestIds = historyList
            .mapNotNull { it.requestId }
            .distinct()

        if (requestIds.isEmpty()) {
            return PaginatedRequestHistory(
                total = 0,
                page = page,
                perPage = perPage,
                items = emptyList()
            )
        }

        val resultItemsByRequestId = resultItemRepository
            .findByRequestsId(requestIds)
            .collectList()
            .awaitSingle()
            .groupBy { it.requestId }

        val content = historyList.map { history ->
            historyMapper.mapHistory(history, resultItemsByRequestId[history.requestId].orEmpty())
        }

        val total = historyRepository.countAll(userId).awaitSingle().toInt()
        return PaginatedRequestHistory(
            total = total,
            page = page,
            perPage = perPage,
            items = content
        )
    }
}
