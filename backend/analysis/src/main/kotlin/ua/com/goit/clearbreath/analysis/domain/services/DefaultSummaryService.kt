package ua.com.goit.clearbreath.analysis.domain.services

import kotlinx.coroutines.reactor.awaitSingle
import kotlin.math.ln
import kotlin.math.exp
import kotlin.math.pow
import org.springframework.stereotype.Service
import ua.com.goit.clearbreath.analysis.domain.models.EvaluationModelMetadataEntity
import ua.com.goit.clearbreath.analysis.domain.models.HistoryItemResult
import ua.com.goit.clearbreath.analysis.domain.repositories.EvaluationModelMetadataRepository

@Service
class DefaultSummaryService(private val modelsRepository: EvaluationModelMetadataRepository) : SummaryService {

    /**
     * Summarizes the list of HistoryItemResult by aggregating probabilities for each diagnose
     * using model AUC as weights in a logit space.
     *
     * @param results List of HistoryItemResult containing model predictions.
     * @return Map of diagnose to aggregated probability.
     */
    override suspend fun summarize(results: List<HistoryItemResult>): Map<String, Double> {
        if (results.isEmpty()) return emptyMap()

        val usedModelNames: Set<String> = results.map { it.modelName }.toSet()

        val aucByModel: Map<String, Double> = modelsRepository.findByModelNameIn(usedModelNames)
            .collectMap(
                EvaluationModelMetadataEntity::modelName,
                EvaluationModelMetadataEntity::modelAuc
            ).awaitSingle()

        return results
            .groupBy { it.diagnose }
            .mapValues { (_, items) ->
                val rs: List<R> = items.map { r ->
                    val auc = aucByModel[r.modelName]
                        ?: throw IllegalArgumentException("Model AUC not found for model ${r.modelName} for request '${r.requestId}'")
                    R(r.probability, auc)
                }

                aggregateByAucLogit(rs)
            }
    }

    private data class R(val result: Double, val modelAUC: Double)

    private fun sigmoid(x: Double) = 1.0 / (1.0 + exp(-x))

    private fun logit(p: Double): Double {
        val pp = p.coerceIn(1e-6, 1.0 - 1e-6)
        return ln(pp / (1.0 - pp))
    }

    private fun weightFromAuc(auc: Double, gamma: Double = 2.0): Double {
        val r = (2.0 * auc - 1.0).coerceAtLeast(0.0)
        return r.pow(gamma)
    }

    private fun aggregateByAucLogit(results: List<R>, gamma: Double = 2.0): Double {
        val items = results.map { r ->
            val w = weightFromAuc(r.modelAUC, gamma)
            w to logit(r.result)
        }.filter { it.first > 0.0 }

        if (items.isEmpty()) return 0.5

        val wSum = items.sumOf { it.first }
        val z = items.sumOf { (w, l) -> w * l } / wSum
        return sigmoid(z)
    }
}