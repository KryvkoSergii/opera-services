package ua.com.goit.clearbreath.analysis.domain.services

import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.model.AnalysisCreateResponse
import ua.com.goit.clearbreath.analysis.model.SourceType

interface AnalysisService {
    fun startAnalysis(file: MultipartFile, sourceType: SourceType): Mono<AnalysisCreateResponse>
}