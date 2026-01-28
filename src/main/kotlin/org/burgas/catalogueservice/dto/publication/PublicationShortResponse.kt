package org.burgas.catalogueservice.dto.publication

import org.burgas.catalogueservice.dto.Response
import java.util.UUID

data class PublicationShortResponse(
    override val id: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val date: String? = null
) : Response
