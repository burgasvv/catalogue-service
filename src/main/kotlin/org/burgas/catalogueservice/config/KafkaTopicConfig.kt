package org.burgas.catalogueservice.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {

    @Bean
    fun identityKafkaTopic(): NewTopic {
        return TopicBuilder.name("identity-topic")
            .partitions(10)
            .replicas(1)
            .build()
    }
}