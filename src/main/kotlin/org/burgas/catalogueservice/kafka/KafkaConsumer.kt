package org.burgas.catalogueservice.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.burgas.catalogueservice.dto.identity.IdentityFullResponse
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer {

    @KafkaListener(groupId = "identity-group-id", topics = ["identity-topic"])
    fun kafkaListenerIdentityFullResponse(consumerRecord: ConsumerRecord<String, IdentityFullResponse>) {
        println("${consumerRecord.topic()} :: ${consumerRecord.value()}")
    }
}