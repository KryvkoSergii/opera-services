package ua.com.goit.clearbreath.analysis.utils

import ua.com.goit.clearbreath.analysis.domain.exceptions.ConvertionFileException
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import java.util.stream.Collectors

object FileConvertingUtil {
    private const val MAX_LENGTH: Int = 25 //25 sec per file
    private const val FREQUENCY: Int = 16000 //16 kHz sampling rate

    fun runFfmpeg(inputFile: String, outputFilePattern: String) {
        val command = listOf(
            "ffmpeg",
            "-y",               // overwrite
            "-i", inputFile,
            "-ac", "1",         // mono
            "-ar", FREQUENCY.toString(),
            "-f", "segment",
            "-segment_time", MAX_LENGTH.toString(),
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