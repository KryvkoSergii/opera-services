package ua.com.goit.clearbreath.analysis.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

@Configuration
@Profile("cloud")
class AwsConfig {

    @Bean
    fun s3ClientOverride(
        credentialsProvider: AwsCredentialsProvider,
        @Value("\${aws.region}") region: Region,
        @Value("\${aws.aws-endpoint-url}") endpointUrl: String
    ): S3AsyncClient {
        val builder = S3AsyncClient.builder().credentialsProvider(credentialsProvider)
        if (endpointUrl.isNotBlank()) {
            builder
                .endpointOverride(URI.create(endpointUrl))
                .serviceConfiguration(
                    S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build()
                )
        }
        return builder.region(region).build()
    }

    @Bean
    fun sqsClientOverride(
        credentialsProvider: AwsCredentialsProvider,
        @Value("\${aws.region}") region: Region,
        @Value("\${aws.aws-endpoint-url}") endpointUrl: String
    ): SqsAsyncClient {
        val builder = SqsAsyncClient.builder().credentialsProvider(credentialsProvider)
        if (endpointUrl.isNotBlank()) {
            builder.endpointOverride(URI.create(endpointUrl))
        }
        return builder.region(region).build()
    }
}