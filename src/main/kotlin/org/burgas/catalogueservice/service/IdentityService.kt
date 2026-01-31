package org.burgas.catalogueservice.service

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.burgas.catalogueservice.dto.identity.IdentityFullResponse
import org.burgas.catalogueservice.dto.identity.IdentityRequest
import org.burgas.catalogueservice.dto.identity.IdentityShortResponse
import org.burgas.catalogueservice.entity.identity.Identity
import org.burgas.catalogueservice.kafka.KafkaProducer
import org.burgas.catalogueservice.mapper.IdentityMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.deleteAndAwait
import org.springframework.data.redis.core.getAndAwait
import org.springframework.data.redis.core.setAndAwait
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
class IdentityService : AsyncCrudService<IdentityRequest, Identity, IdentityShortResponse, IdentityFullResponse> {

    private final val identityMapper: IdentityMapper
    private final val kafkaProducer: KafkaProducer

    @Qualifier(value = "identityReactiveRedisTemplate")
    private final val reactiveRedisTemplate: ReactiveRedisTemplate<String, IdentityFullResponse>

    constructor(
        identityMapper: IdentityMapper,
        kafkaProducer: KafkaProducer,
        reactiveRedisTemplate: ReactiveRedisTemplate<String, IdentityFullResponse>
    ) {
        this.identityMapper = identityMapper
        this.kafkaProducer = kafkaProducer
        this.reactiveRedisTemplate = reactiveRedisTemplate
    }

    override suspend fun findEntity(id: UUID): Identity {
        return this.identityMapper.identityRepository.findById(id)
            ?: throw IllegalArgumentException("Identity not found")
    }

    override suspend fun findById(id: UUID): IdentityFullResponse {
        val identityFullResponseFromRedis =
            this.reactiveRedisTemplate.opsForValue().getAndAwait("identityFullResponse::$id")
        if (identityFullResponseFromRedis != null) {
            return identityFullResponseFromRedis
        } else {
            val identityFullResponseFromPostgres = this.identityMapper.toFullResponse(this.findEntity(id))
            this.reactiveRedisTemplate.opsForValue()
                .setAndAwait("identityFullResponse::$id", identityFullResponseFromPostgres)
            return identityFullResponseFromPostgres
        }
    }

    override suspend fun findAll(): List<IdentityShortResponse> {
        return this.identityMapper.identityRepository.findAll()
            .map { this.identityMapper.toShortResponse(it) }
            .toList()
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun create(request: IdentityRequest) {
        val identity = this.identityMapper.toEntity(request)
        val saved = this.identityMapper.identityRepository.save(identity)
        val identityFullResponse = this.identityMapper.toFullResponse(saved)
        this.kafkaProducer.sendIdentityFullResponse(identityFullResponse)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun update(request: IdentityRequest) {
        if (request.id == null) {
            throw IllegalArgumentException("Identity id is null")
        }
        val identity = this.identityMapper.toEntity(request)
        val saved = this.identityMapper.identityRepository.save(identity)
        val identityFullResponse =
            this.reactiveRedisTemplate.opsForValue().getAndAwait("identityFullResponse::${saved.id}")
        if (identityFullResponse != null) {
            this.reactiveRedisTemplate.opsForValue().deleteAndAwait("identityFullResponse::${saved.id}")
        }
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun delete(id: UUID) {
        val identity = this.identityMapper.identityRepository.findById(id)
        if (identity != null) {
            val identityFullResponse =
                this.reactiveRedisTemplate.opsForValue().getAndAwait("identityFullResponse::${id}")
            if (identityFullResponse != null) {
                this.reactiveRedisTemplate.opsForValue().deleteAndAwait("identityFullResponse::${id}")
            }
            this.identityMapper.identityRepository.delete(identity)
        } else {
            throw IllegalArgumentException("Identity not found")
        }
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    suspend fun changePassword(request: IdentityRequest) {
        if (request.id == null) {
            throw IllegalArgumentException("Identity id is null")
        }
        if (request.password == null) {
            throw IllegalArgumentException("Identity password is null")
        }
        val identity = this.identityMapper.identityRepository.findById(request.id)
            ?: throw IllegalArgumentException("Identity not found")
        if (this.identityMapper.passwordEncoder.matches(request.password, identity.password)) {
            throw IllegalArgumentException("New and old password matched")
        }
        val identityFullResponse =
            this.reactiveRedisTemplate.opsForValue().getAndAwait("identityFullResponse::${request.id}")
        if (identityFullResponse != null) {
            this.reactiveRedisTemplate.opsForValue().deleteAndAwait("identityFullResponse::${request.id}")
        }
        identity.apply {
            this.password = identityMapper.passwordEncoder.encode(request.password)
                ?: throw IllegalArgumentException("New encoded password is null")
        }
        this.identityMapper.identityRepository.save(identity)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    suspend fun changeStatus(request: IdentityRequest) {
        if (request.id == null) {
            throw IllegalArgumentException("Identity id is null")
        }
        if (request.enabled == null) {
            throw IllegalArgumentException("Identity enabled is null")
        }
        val identity = this.identityMapper.identityRepository.findById(request.id)
            ?: throw IllegalArgumentException("Identity not found")
        if (identity.enabled == request.enabled) {
            throw IllegalArgumentException("Identity statuses matched")
        }
        val identityFullResponse =
            this.reactiveRedisTemplate.opsForValue().getAndAwait("identityFullResponse::${request.id}")
        if (identityFullResponse != null) {
            this.reactiveRedisTemplate.opsForValue().deleteAndAwait("identityFullResponse::${request.id}")
        }
        identity.apply {
            this.enabled = request.enabled
        }
        this.identityMapper.identityRepository.save(identity)
    }
}