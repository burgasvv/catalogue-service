package org.burgas.catalogueservice.config

import org.burgas.catalogueservice.dto.identity.IdentityFullResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun identityReactiveRedisTemplate(connectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, IdentityFullResponse> {
        val keySerializer = StringRedisSerializer.UTF_8
        val valueSerializer = JacksonJsonRedisSerializer(IdentityFullResponse::class.java)
        val context = RedisSerializationContext
            .newSerializationContext<String, IdentityFullResponse>(keySerializer)
            .value(valueSerializer)
            .build()
        return ReactiveRedisTemplate(connectionFactory, context)
    }
}