package com.noizy.infrastructure.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.noizy.domain.event.NoizyEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

@Service
class EventPublisher(
    private val sqs: SqsClient,
    private val sns: SnsClient,
    private val objectMapper: ObjectMapper,
    @Value("\${noizy.aws.sqs.track-events-queue-url:}") private val queueUrl: String,
    @Value("\${noizy.aws.sns.topic-arn:}") private val topicArn: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun publish(event: NoizyEvent) {
        val payload = objectMapper.writeValueAsString(event)
        if (topicArn.isNotBlank()) {
            sns.publish(PublishRequest.builder().topicArn(topicArn).message(payload).build())
            logger.info("Published Noizy event to SNS type={} id={}", event.type, event.id)
            return
        }
        if (queueUrl.isNotBlank()) {
            sqs.sendMessage(SendMessageRequest.builder().queueUrl(queueUrl).messageBody(payload).build())
            logger.info("Published Noizy event to SQS type={} id={}", event.type, event.id)
        } else {
            logger.info("Noizy event emitted without queue configuration type={} id={}", event.type, event.id)
        }
    }
}
