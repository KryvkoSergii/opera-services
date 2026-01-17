package ua.com.goit.clearbreath.analysis.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.sqs.SqsAsyncClient

@Configuration
@Profile("cloud")
class AwsConfig {

    @Bean
    fun awsCredentialsProvider(
        @Value("\${aws.access-key-id}") accessKey: String,
        @Value("\${aws.secret-access-key}") secretKey: String
    ): AwsCredentialsProvider =
        StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        )

    @Bean
    fun s3Client(
        credentialsProvider: AwsCredentialsProvider,
        @Value("\${aws.region}") region: Region
    ): S3AsyncClient =
        S3AsyncClient.builder()
            .credentialsProvider(credentialsProvider)
            .region(region)
            .build()

    @Bean
    fun sqsClient(
        credentialsProvider: AwsCredentialsProvider,
        @Value("\${aws.region}") region: Region
    ): SqsAsyncClient =
        SqsAsyncClient.builder()
            .credentialsProvider(credentialsProvider)
            .region(region)
            .build()
}