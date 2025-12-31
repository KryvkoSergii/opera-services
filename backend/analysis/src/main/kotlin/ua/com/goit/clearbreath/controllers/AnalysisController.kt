package ua.com.goit.clearbreath.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ua.com.goit.clearbreath.api.AnalysisApi
import ua.com.goit.clearbreath.domain.services.AnalysisService
import ua.com.goit.clearbreath.model.AnalysisCreateResponse
import ua.com.goit.clearbreath.model.SourceType
import java.nio.file.Files
import java.nio.file.Paths

@RestController
class AnalysisController(private val service: AnalysisService) : AnalysisApi {

    override fun createAnalysisRequest(audioFile: MultipartFile, sourceType: String):
            ResponseEntity<AnalysisCreateResponse> {
        val baseDir = Paths.get("").toAbsolutePath()
        val uploadDir = baseDir.resolve("tmp")
        Files.createDirectories(uploadDir)

        val fileName = audioFile.originalFilename ?: "audio.wav"
        val target = uploadDir.resolve(fileName).toFile()

        audioFile.transferTo(target)

        service.startAnalysis(audioFile, SourceType.forValue(sourceType)).block().let {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(it)
        }
    }

}