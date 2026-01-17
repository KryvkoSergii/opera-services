package ua.com.goit.clearbreath.analysis.controllers

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import ua.com.goit.clearbreath.analysis.api.AnalysisApi.Companion.PATH_CREATE_ANALYSIS_REQUEST
import ua.com.goit.clearbreath.analysis.domain.mapper.ProcessingStatusMapper
import ua.com.goit.clearbreath.analysis.domain.mapper.SourceTypeMapper
import ua.com.goit.clearbreath.analysis.domain.services.AnalysisService
import ua.com.goit.clearbreath.analysis.model.AnalysisCreateResponse
import ua.com.goit.clearbreath.analysis.model.SourceType
import ua.com.goit.clearbreath.analysis.utils.TimeZoneUtils
import java.time.OffsetDateTime
import java.time.ZoneId

@RestController
class AnalysisController(
    private val service: AnalysisService,
    private val sourceTypeMapper: SourceTypeMapper,
    private val processingStatusMapper: ProcessingStatusMapper
) {

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
        audioFile: FilePart,
        @RequestHeader(value = "X-Timezone", required = false) xTimezone: String?
    ): ResponseEntity<AnalysisCreateResponse> {

        val fileDesc = AnalysisService.FileDesc(
            getExtension(audioFile.filename()),
            audioFile.headers().contentType.toString(),
            audioFile.content()
        )

        service.startAnalysis(fileDesc, sourceTypeMapper.toEntity(sourceType)).let {

            val response = AnalysisCreateResponse(
                it.requestId.toString(),
                processingStatusMapper.toDto(it.processingStatus),
                it.createdAt?.atZone(TimeZoneUtils.resolveZoneId(xTimezone))?.toOffsetDateTime() ?: OffsetDateTime.now()
            )

            return ResponseEntity.status(HttpStatus.CREATED).body(response)
        }
    }

    private fun getExtension(filename: String): String {
        return filename.substringAfterLast('.', "").lowercase()
    }

}