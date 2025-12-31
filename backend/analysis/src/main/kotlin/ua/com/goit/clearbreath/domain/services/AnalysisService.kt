package ua.com.goit.clearbreath.domain.services

import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.model.AnalysisCreateResponse
import ua.com.goit.clearbreath.model.SourceType

interface AnalysisService {
    fun startAnalysis(file: MultipartFile, sourceType: SourceType): Mono<AnalysisCreateResponse>
}