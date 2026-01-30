package org.burgas.catalogueservice.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.burgas.catalogueservice.dto.identity.IdentityFullResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer
import kotlin.reflect.jvm.jvmName

@Configuration
class KafkaConsumerConfig {

    @Bean
    fun identityConsumerConfig(): Map<String, Any> {
        return mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG to "identity-group-id",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JacksonJsonDeserializer::class.java,
            JacksonJsonDeserializer.TYPE_MAPPINGS
                    to "org.burgas.catalogueservice.dto.identity.IdentityFullResponse:org.burgas.catalogueservice.dto.identity.IdentityFullResponse"
        )
    }

    @Bean
    fun identityKafkaConsumer(): KafkaConsumer<String, IdentityFullResponse> {
        val kafkaConsumer = KafkaConsumer<String, IdentityFullResponse>(this.identityConsumerConfig())
        kafkaConsumer.subscribe(listOf("identity-topic"))
        return kafkaConsumer
    }
}