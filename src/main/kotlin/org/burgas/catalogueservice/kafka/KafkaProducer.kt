package org.burgas.catalogueservice.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.burgas.catalogueservice.dto.identity.IdentityFullResponse
import org.springframework.stereotype.Component

@Component
class KafkaProducer {

    private final val kafkaProducer: KafkaProducer<String, IdentityFullResponse>

    constructor(kafkaProducer: KafkaProducer<String, IdentityFullResponse>) {
        this.kafkaProducer = kafkaProducer
    }

    fun sendIdentityFullResponse(identityFullResponse: IdentityFullResponse) {
        val producerRecord = ProducerRecord<String, IdentityFullResponse>("identity-topic", identityFullResponse)
        this.kafkaProducer.send(producerRecord)
    }
}