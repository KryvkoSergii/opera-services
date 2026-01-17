package ua.com.goit.clearbreath.analysis.domain.services

import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux
import ua.com.goit.clearbreath.analysis.domain.models.HistoryEntity
import ua.com.goit.clearbreath.analysis.domain.models.SourceTypeEntity

interface AnalysisService {
    suspend fun startAnalysis(fileDesc: FileDesc, sourceType: SourceTypeEntity): HistoryEntity

    data class FileDesc(
        val extension: String,
        val contentType: String,
        val fileContent: Flux<DataBuffer>
    )
}