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

    @Query(
        """
        SELECT id, processing_status, created_at, source_type, recommendation
        FROM history
        WHERE user_id = :userId
        ORDER BY created_at DESC
        LIMIT :#{#pageable.pageSize}
        OFFSET :#{#pageable.offset}
    """
    )
    fun findPage(pageable: Pageable, user: UUID): Flux<HistoryEntity>

    @Query(
        """
        SELECT COUNT(id) FROM history WHERE user_id = :user
    """
    )
    fun countAll(user: UUID): Mono<Long>
}