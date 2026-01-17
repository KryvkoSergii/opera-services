package ua.com.goit.clearbreath.analysis.domain.services

import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.Probability

@Component
class AdvisingService {

    fun giveAdvise(probability: Double): AdviceResult {
        return when {
            probability < 0.3 -> AdviceResult(Probability.LOW, "Low risk. Keep healthy lifestyle.")
            probability < 0.7 -> AdviceResult(Probability.MODERATE, "Moderate risk. Consider visit doctor")
            else -> AdviceResult(Probability.HIGH, "High risk detected! Please, visit doctor as soon as possible.")
        }
    }

    data class AdviceResult(
        val status: Probability,
        val advice: String
    )
}