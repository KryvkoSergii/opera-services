package ua.com.goit.clearbreath.analysis.domain.services

import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux
import ua.com.goit.clearbreath.analysis.model.AnalysisCreateResponse
import ua.com.goit.clearbreath.analysis.model.SourceType

interface AnalysisService {
    suspend fun startAnalysis(fileDesc: FileDesc, sourceType: SourceType): AnalysisCreateResponse

    data class FileDesc(
        val extension: String,
        val contentType: String,
        val fileContent: Flux<DataBuffer>
    )
}