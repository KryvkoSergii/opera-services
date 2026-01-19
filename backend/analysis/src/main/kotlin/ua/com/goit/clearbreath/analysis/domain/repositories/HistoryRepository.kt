package ua.com.goit.clearbreath.analysis.domain.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.models.HistoryEntity
import java.util.UUID

@Repository
interface HistoryRepository : ReactiveCrudRepository<HistoryEntity, UUID> {

    fun findAllByUserOrderByCreatedAtDesc(user: UUID, pageable: Pageable): Flux<HistoryEntity>

    @Query(
        """
        SELECT COUNT(h.request_id) FROM history as h WHERE h.user_id = :user
    """
    )
    fun countAll(user: UUID): Mono<Long>
}