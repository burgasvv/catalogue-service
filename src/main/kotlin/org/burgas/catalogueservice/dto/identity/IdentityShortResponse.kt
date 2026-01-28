package org.burgas.catalogueservice.dto.identity

import org.burgas.catalogueservice.dto.Response
import java.util.UUID

data class IdentityShortResponse(
    override val id: UUID? = null,
    val username: String? = null,
    val email: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val patronymic: String? = null
) : Response
