package ua.com.goit.clearbreath.analysis.controllers

import jakarta.validation.Valid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import ua.com.goit.clearbreath.analysis.api.AnalysisApi.Companion.PATH_CREATE_ANALYSIS_REQUEST
import ua.com.goit.clearbreath.analysis.domain.services.AnalysisService
import ua.com.goit.clearbreath.analysis.model.AnalysisCreateResponse
import ua.com.goit.clearbreath.analysis.model.SourceType
import java.nio.file.Files
import java.nio.file.Paths

@RestController
class AnalysisController(private val service: AnalysisService) {

    @RequestMapping(
        method = [RequestMethod.POST],
        value = [PATH_CREATE_ANALYSIS_REQUEST],
        produces = ["application/json"],
        consumes = ["multipart/form-data"]
    )
     suspend fun createAnalysisRequest(
        @Valid @RequestParam(value = "source-type", required = true)
        sourceType: SourceType,
        @Valid @RequestPart("audio-file", required = true)
        audioFile: FilePart
    ):
            ResponseEntity<AnalysisCreateResponse> {
        val baseDir = Paths.get("").toAbsolutePath()
        val uploadDir = baseDir.resolve("tmp")
        withContext(Dispatchers.IO) {
            Files.createDirectories(uploadDir)
        }

        val fileName = audioFile.filename() ?: "audio.wav"
        val target = uploadDir.resolve(fileName).toFile()

        audioFile.transferTo(target)

        service.startAnalysis(sourceType).let {
            return ResponseEntity.status(HttpStatus.CREATED).body(it)
        }
    }

}