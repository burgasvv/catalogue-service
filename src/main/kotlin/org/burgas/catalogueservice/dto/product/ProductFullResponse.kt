package org.burgas.catalogueservice.dto.product

import org.burgas.catalogueservice.dto.Response
import org.burgas.catalogueservice.dto.category.CategoryShortResponse
import org.burgas.catalogueservice.dto.publication.PublicationWithCatalogueResponse
import java.util.UUID

data class ProductFullResponse(
    override val id: UUID? = null,
    val name: String? = null,
    val category: CategoryShortResponse? = null,
    val description: String? = null,
    val price: Double? = null,
    val publications: List<PublicationWithCatalogueResponse>? = null
) : Response
