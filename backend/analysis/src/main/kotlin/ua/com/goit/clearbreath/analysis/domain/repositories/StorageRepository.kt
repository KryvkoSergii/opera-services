package ua.com.goit.clearbreath.analysis.domain.repositories

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.nio.ByteBuffer
import java.util.UUID

@Component
class StorageRepository(
    private val s3Client: S3AsyncClient,
    @Value("\${s3.bucket}")
    private val bucket: String
) {

    fun saveOriginalFile(requestId: UUID, extension: String, content: Flux<DataBuffer>): String {
        val key = "${getKeyForOriginalFile(requestId)}.$extension"

        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build()

        val publisher = AsyncRequestBody.fromPublisher(
            content.map { db ->
                try {
                    val buffer = ByteBuffer.allocate(db.readableByteCount())
                    db.toByteBuffer(buffer)
                    buffer.flip()
                    buffer
                } finally {
                    DataBufferUtils.release(db)
                }
            }
        )

        s3Client.putObject(request, publisher).
        return key
    }

    private fun getKeyForOriginalFile(requestId: UUID): String {
        return key("original", requestId)
    }

    private fun key(type: String, requestId: UUID): String {
        return "$type/$requestId"
    }
}