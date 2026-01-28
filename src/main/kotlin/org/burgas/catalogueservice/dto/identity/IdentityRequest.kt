package org.burgas.catalogueservice.dto.identity

import org.burgas.catalogueservice.dto.Request
import org.burgas.catalogueservice.entity.identity.Authority
import java.util.UUID

data class IdentityRequest(
    override val id: UUID? = null,
    val authority: Authority? = null,
    val username: String? = null,
    val password: String? = null,
    val email: String? = null,
    val enabled: Boolean? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val patronymic: String? = null
) : Request
