package org.burgas.catalogueservice.repository

import org.burgas.catalogueservice.entity.identity.Identity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface IdentityRepository : CoroutineCrudRepository<Identity, UUID> {

    suspend fun findIdentityByEmail(email: String): Identity?
}