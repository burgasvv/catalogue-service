package org.burgas.catalogueservice.dto.catalogue

import org.burgas.catalogueservice.dto.Request
import java.util.UUID

data class CatalogueRequest(
    override val id: UUID? = null,
    val name: String? = null,
    val description: String? = null
) : Request
