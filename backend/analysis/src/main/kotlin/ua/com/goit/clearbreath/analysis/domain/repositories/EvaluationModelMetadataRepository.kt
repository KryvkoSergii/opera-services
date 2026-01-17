package ua.com.goit.clearbreath.analysis.domain.repositories

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.models.EvaluationModelMetadataEntity
import java.util.UUID

@Repository
interface EvaluationModelMetadataRepository: ReactiveCrudRepository<EvaluationModelMetadataEntity, UUID> {
    fun findByModelNameIn(modelNames: Collection<String>): Flux<EvaluationModelMetadataEntity>
}