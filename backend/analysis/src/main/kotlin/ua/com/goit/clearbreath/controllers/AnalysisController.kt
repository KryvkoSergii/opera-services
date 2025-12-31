package ua.com.goit.clearbreath.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ua.com.goit.clearbreath.api.AnalysisApi
import ua.com.goit.clearbreath.model.AnalysisCreateMeta
import ua.com.goit.clearbreath.model.AnalysisCreateResponse
import java.nio.file.Files
import java.nio.file.Paths

@RestController
class AnalysisController : AnalysisApi {

    override fun createAnalysisRequest(
        audioFile: MultipartFile, metaData: AnalysisCreateMeta?
    ): ResponseEntity<AnalysisCreateResponse> {
        val baseDir = Paths.get("").toAbsolutePath()
        val uploadDir = baseDir.resolve("tmp")
        Files.createDirectories(uploadDir)

        val fileName = audioFile.originalFilename ?: "audio.wav"
        val target = uploadDir.resolve(fileName).toFile()

        audioFile.transferTo(target)
        return ResponseEntity(HttpStatus.CREATED)
    }

}