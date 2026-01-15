package ua.com.goit.clearbreath.analysis.domain.repositories

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import ua.com.goit.clearbreath.analysis.domain.models.HistoryItemResult
import ua.com.goit.clearbreath.analysis.domain.models.HistoryProcessingItem
import java.util.*

@Repository
interface HistoryItemResultRepository : ReactiveCrudRepository<HistoryItemResult, UUID> {
    @Query(
        """
        SELECT result_id, item_id, request_id, model_name, diagnose, probability
        FROM history_item_result item
        WHERE request_id in (:requestIds)
        """
    )
    fun findByRequestsId(requestIds: Collection<UUID>): Flux<HistoryProcessingItem>
}