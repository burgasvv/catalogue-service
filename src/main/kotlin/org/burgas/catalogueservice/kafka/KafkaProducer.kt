package org.burgas.catalogueservice.kafka

import org.burgas.catalogueservice.dto.identity.IdentityFullResponse
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer {

    private final val kafkaTemplate: KafkaTemplate<String, IdentityFullResponse>

    constructor(kafkaTemplate: KafkaTemplate<String, IdentityFullResponse>) {
        this.kafkaTemplate = kafkaTemplate
    }

    fun sendIdentityFullResponse(identityFullResponse: IdentityFullResponse) {
        this.kafkaTemplate.send("identity-topic", identityFullResponse)
    }
}