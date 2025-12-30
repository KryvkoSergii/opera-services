package ua.com.goit.clearbreath.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ua.com.goit.clearbreath.api.AnalysisApi
import ua.com.goit.clearbreath.model.AnalysisCreateMeta
import ua.com.goit.clearbreath.model.AnalysisCreateResponse

@RestController
class AnalysisController: AnalysisApi {

    override suspend fun createAnalysisRequest(file: MultipartFile, meta: AnalysisCreateMeta):
            ResponseEntity<AnalysisCreateResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

}