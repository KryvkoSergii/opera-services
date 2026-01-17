package ua.com.goit.clearbreath.analysis.domain.repositories

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.nio.file.Path

@Component
@Profile("cloud")
class StorageRepository(
    private val s3Client: S3AsyncClient,
    @Value("\${s3.bucket}")
    private val bucket: String
) {

    fun saveOriginalFileToRemote(fileOnLocalDisk: Path): Mono<String> {
        val fileName = getKeyForOriginalFile(fileOnLocalDisk.fileName.toString())
        val request = PutObjectRequest.builder().bucket(bucket).key(fileName).build()
        val body = AsyncRequestBody.fromFile(fileOnLocalDisk)
        return Mono.fromFuture(s3Client.putObject(request, body)).thenReturn(fileName)
    }

    private fun getKeyForOriginalFile(fileName: String): String {
        return key("original", fileName)
    }

    private fun key(type: String, fileName: String): String {
        return "$type/$fileName"
    }
}