package org.burgas.catalogueservice.mapper

import org.burgas.catalogueservice.dto.identity.IdentityFullResponse
import org.burgas.catalogueservice.dto.identity.IdentityRequest
import org.burgas.catalogueservice.dto.identity.IdentityShortResponse
import org.burgas.catalogueservice.entity.identity.Identity
import org.burgas.catalogueservice.repository.IdentityRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IdentityMapper : Mapper<IdentityRequest, Identity, IdentityShortResponse, IdentityFullResponse> {

    final val passwordEncoder: PasswordEncoder
    final val identityRepository: IdentityRepository

    constructor(identityRepository: IdentityRepository, passwordEncoder: PasswordEncoder) {
        this.identityRepository = identityRepository
        this.passwordEncoder = passwordEncoder
    }

    override suspend fun toEntity(request: IdentityRequest): Identity {
        val identity = this.identityRepository.findById(request.id ?: UUID.randomUUID())
        return if (identity != null) {
            Identity().apply {
                this.id = identity.id
                this.authority = request.authority ?: identity.authority
                this.username = request.username ?: identity.username
                this.password = identity.password
                this.email = request.email ?: identity.email
                this.enabled = identity.enabled
                this.firstname = request.firstname ?: identity.firstname
                this.lastname = request.lastname ?: identity.lastname
                this.patronymic = request.patronymic ?: identity.patronymic
            }
        } else {
            Identity().apply {
                this.authority = request.authority ?: throw IllegalArgumentException("Identity authority is null")
                this.username = request.username ?: throw IllegalArgumentException("Identity username is null")
                val newPassword = passwordEncoder.encode(
                    request.password ?: throw IllegalArgumentException("Identity password is null")
                )
                this.password = newPassword ?: throw IllegalArgumentException("New password is null")
                this.email = request.email ?: throw IllegalArgumentException("Identity email in null")
                this.enabled = request.enabled ?: true
                this.firstname = request.firstname ?: throw IllegalArgumentException("Identity firstname is null")
                this.lastname = request.lastname ?: throw IllegalArgumentException("Identity lastname is null")
                this.patronymic = request.patronymic ?: throw IllegalArgumentException("identity patronymic is null")
            }
        }
    }

    override suspend fun toShortResponse(entity: Identity): IdentityShortResponse {
        return IdentityShortResponse(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            firstname = entity.firstname,
            lastname = entity.lastname,
            patronymic = entity.patronymic
        )
    }

    override suspend fun toFullResponse(entity: Identity): IdentityFullResponse {
        return IdentityFullResponse(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            firstname = entity.firstname,
            lastname = entity.lastname,
            patronymic = entity.patronymic
        )
    }
}