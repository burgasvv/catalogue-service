package org.burgas.catalogueservice.dto.publication

import org.burgas.catalogueservice.dto.Request
import java.time.LocalDate
import java.util.UUID

data class PublicationRequest(
    override val id: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val date: LocalDate? = null,
    val catalogueId: UUID? = null
) : Request
