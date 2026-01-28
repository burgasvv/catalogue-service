package org.burgas.catalogueservice.dto.publication

import org.burgas.catalogueservice.dto.Response
import org.burgas.catalogueservice.dto.catalogue.CatalogueShortResponse
import java.util.UUID

data class PublicationWithCatalogueResponse(
    override val id: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val date: String? = null,
    val catalogue: CatalogueShortResponse? = null
) : Response
