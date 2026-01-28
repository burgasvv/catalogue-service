package org.burgas.catalogueservice.service

import kotlinx.coroutines.reactor.mono
import org.burgas.catalogueservice.entity.identity.IdentityDetails
import org.burgas.catalogueservice.mapper.IdentityMapper
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
class IdentityDetailsService : ReactiveUserDetailsService {

    private final val identityMapper: IdentityMapper

    constructor(identityMapper: IdentityMapper) {
        this.identityMapper = identityMapper
    }

    override fun findByUsername(username: String): Mono<UserDetails> = mono {
        val identity = identityMapper.identityRepository.findIdentityByEmail(username)
            ?: throw IllegalArgumentException("Identity not found")
        IdentityDetails(identity)
    }
}