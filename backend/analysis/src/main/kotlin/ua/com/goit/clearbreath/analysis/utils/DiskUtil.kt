package ua.com.goit.clearbreath.analysis.utils

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

object DiskUtil {
    val TEMPORARY_DIRECTORY: Path = getDir("./temp-files")
    val ORIGINALS_DIRECTORY: Path = getDir(TEMPORARY_DIRECTORY.resolve("originals").toString())
    val CONVERTED_DIRECTORY: Path = getDir(TEMPORARY_DIRECTORY.resolve("converted").toString())

    fun saveOriginalToTempDirectoryOnDisk(content: Flux<DataBuffer>, filename: String): Mono<Path> {
        val filename = ORIGINALS_DIRECTORY.resolve(filename)

        return DataBufferUtils.write(
            content,
            filename,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        )
            .then(Mono.just(filename))
    }

    private fun getDir(path: String): Path {
        val dir = Path.of(path)
        if (!Files.exists(dir)) {
            Files.createDirectory(dir)
        }
        return dir
    }
}