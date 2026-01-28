package org.burgas.catalogueservice.dto.catalogue

import org.burgas.catalogueservice.dto.Response
import org.burgas.catalogueservice.dto.publication.PublicationShortResponse
import java.util.UUID

data class CatalogueFullResponse(
    override val id: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val publications: List<PublicationShortResponse>? = null
) : Response
