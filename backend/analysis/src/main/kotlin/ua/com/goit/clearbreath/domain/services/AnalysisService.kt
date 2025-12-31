package ua.com.goit.clearbreath.domain.services

import org.springframework.web.multipart.MultipartFile
import ua.com.goit.clearbreath.model.AnalysisCreateResponse

interface AnalysisService {
    suspend fun startAnalysis(file: MultipartFile, sourceType: String): AnalysisCreateResponse
}