package ua.com.goit.clearbreath.analysis.domain.repositories

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import ua.com.goit.clearbreath.analysis.domain.models.HistoryProcessingItem
import java.util.*

@Repository
interface HistoryProcessingItemRepository : ReactiveCrudRepository<HistoryProcessingItem, UUID> {
    @Query(
        """
        SELECT item_id, request_id, file_location, processing_status
        FROM history_processing_item item
        WHERE request_id in (:requestIds)
        """
    )
    fun findByRequestsId(requestIds: Collection<UUID>): Flux<HistoryProcessingItem>
}