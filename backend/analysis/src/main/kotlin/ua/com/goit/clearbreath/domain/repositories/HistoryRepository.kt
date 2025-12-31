package ua.com.goit.clearbreath.domain.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import ua.com.goit.clearbreath.domain.models.HistoryEntity
import java.util.UUID

interface HistoryRepository : ReactiveCrudRepository<HistoryEntity, UUID> {

    @Query("""
        SELECT id, processing_status, created_at, source_type, recommendation
        FROM history
        ORDER BY created_at DESC
        LIMIT :#{#pageable.pageSize}
        OFFSET :#{#pageable.offset}
    """)
    fun findPage(pageable: Pageable): Flux<HistoryEntity>
}