package ua.com.goit.clearbreath.analysis.tasks

import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.exceptions.ConvertionFileException
import ua.com.goit.clearbreath.analysis.domain.models.HistoryProcessingItem
import ua.com.goit.clearbreath.analysis.domain.models.ProcessingStatusEntity
import ua.com.goit.clearbreath.analysis.domain.repositories.HistoryProcessingItemRepository
import ua.com.goit.clearbreath.analysis.utils.DiskUtil
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.util.*
import java.util.stream.Collectors

@Component
class FileConvertProcessor(
    private val itemRepository: HistoryProcessingItemRepository
) {
    private val maxLength: Int = 25 //25 sec per file
    private val frequency: Int = 16000 //16 kHz sampling rate

    @Async("taskExecutor")
    @EventListener
    fun on(event: ConvertFileEvent) {
        val requestId = event.requestId;
        val outputFilePattern = "${DiskUtil.CONVERTED_DIRECTORY}/${requestId}_%03d.wav"

        runFfmpeg(event.onLocalDisk.toString(), outputFilePattern)
        val files = listConvertedFiles(requestId)

        files
            .map {
                it //upload converted file to storage
            }
            .map {
                itemRepository.save(
                    HistoryProcessingItem(
                        requestId = requestId,
                        fileLocation = it.toString(),
                        processingStatus = ProcessingStatusEntity.UPLOADED
                    )
                )
            }
            .map{
                println("Saved item: ${it.map { e-> e.fileLocation }.block(Duration.ofMinutes(1))}}")
            }

        //download file form storage
        //convert file to wav
        //split to several files if needed
        //upload converted file to storage
        //publish StartInferenceEvents for each file part
    }

    fun runFfmpeg(inputFile: String, outputFilePattern: String) {
        val command = listOf(
            "ffmpeg",
            "-y",               // overwrite
            "-i", inputFile,
            "-ac", "1",         // mono
            "-ar", frequency.toString(),     // 16 kHz
            "-f", "segment",
            "-segment_time", maxLength.toString(),
            outputFilePattern
        )

        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .start()

        process.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                println("[ffmpeg] $line")
            }
        }

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw ConvertionFileException("Unable to convert. ffmpeg exited with $exitCode")
        }
    }

    fun listConvertedFiles(requestId: UUID): List<Path> {
        val dir = DiskUtil.CONVERTED_DIRECTORY

        return Files.list(dir)
            .filter { path ->
                val name = path.fileName.toString()
                name.startsWith("${requestId}_") && name.endsWith(".wav")
            }
            .sorted()
            .collect(Collectors.toList())
    }
}