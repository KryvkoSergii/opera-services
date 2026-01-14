package ua.com.goit.clearbreath.analysis.domain.services

import ua.com.goit.clearbreath.analysis.model.AnalysisCreateResponse
import ua.com.goit.clearbreath.analysis.model.SourceType

interface AnalysisService {
    suspend fun startAnalysis(sourceType: SourceType): AnalysisCreateResponse
}