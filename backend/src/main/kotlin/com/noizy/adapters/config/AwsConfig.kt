package com.noizy.adapters.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
class AwsConfig(
    @Value("\${noizy.aws.region}") private val region: String,
    @Value("\${noizy.aws.endpoint:}") private val endpoint: String
) {
    @Bean
    fun s3Client(): S3Client {
        val builder = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
        if (endpoint.isNotBlank()) {
            builder.endpointOverride(URI.create(endpoint)).forcePathStyle(true)
        }
        return builder.build()
    }

    @Bean
    fun sqsClient(): SqsClient {
        val builder = SqsClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
        if (endpoint.isNotBlank()) {
            builder.endpointOverride(URI.create(endpoint))
        }
        return builder.build()
    }

    @Bean
    fun snsClient(): SnsClient {
        val builder = SnsClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
        if (endpoint.isNotBlank()) {
            builder.endpointOverride(URI.create(endpoint))
        }
        return builder.build()
    }
}
