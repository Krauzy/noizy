package com.noizy.adapters.output.messaging

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

@Component
class EventConsumer(
    private val sqs: SqsClient,
    @Value("\${noizy.aws.sqs.track-events-queue-url:}") private val queueUrl: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 15000)
    fun pollEvents() {
        if (queueUrl.isBlank()) return

        runCatching {
            val response = sqs.receiveMessage(
                ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(5)
                    .waitTimeSeconds(1)
                    .build()
            )
            response.messages().forEach { message ->
                logger.info("Consumed Noizy event messageId={} body={}", message.messageId(), message.body())
                sqs.deleteMessage(
                    DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build()
                )
            }
        }.onFailure {
            logger.warn("Failed to consume Noizy events: {}", it.message)
        }
    }
}
