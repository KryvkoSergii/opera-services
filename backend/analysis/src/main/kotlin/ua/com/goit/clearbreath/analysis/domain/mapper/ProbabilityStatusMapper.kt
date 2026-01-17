package ua.com.goit.clearbreath.analysis.domain.mapper

import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.Probability
import ua.com.goit.clearbreath.analysis.model.ProbabilityStatus

@Component
class ProbabilityStatusMapper {
    fun toDto(status: Probability): ProbabilityStatus = ProbabilityStatus.valueOf(status.name)
    fun toEntity(status: ProbabilityStatus): Probability = Probability.valueOf(status.name)
}