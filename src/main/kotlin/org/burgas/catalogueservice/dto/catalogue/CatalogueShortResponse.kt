package org.burgas.catalogueservice.dto.catalogue

import org.burgas.catalogueservice.dto.Response
import java.util.UUID

data class CatalogueShortResponse(
    override val id: UUID? = null,
    val name: String? = null,
    val description: String? = null
) : Response
