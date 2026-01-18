package ua.com.goit.clearbreath.analysis.infra

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageResponse
import ua.com.goit.clearbreath.analysis.events.InferenceStartEventPayload

@Component
class QueueEventPublisher(
    private val sqs: SqsAsyncClient,
    private val objectMapper: ObjectMapper,
    @Value("\${sqs.inference-start-queue-url}") private val queueUrl: String
) {

    fun publishStartInference(payload: InferenceStartEventPayload): Mono<SendMessageResponse> {
        val body = objectMapper.writeValueAsString(payload)

        val request = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(body)
            .build()

        return Mono.fromFuture(sqs.sendMessage(request))
    }
}