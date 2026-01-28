package org.burgas.catalogueservice.dto.publication

import org.burgas.catalogueservice.dto.Response
import org.burgas.catalogueservice.dto.catalogue.CatalogueShortResponse
import org.burgas.catalogueservice.dto.product.ProductWithCategoryResponse
import java.util.UUID

data class PublicationFullResponse(
    override val id: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val date: String? = null,
    val catalogue: CatalogueShortResponse? = null,
    val products: List<ProductWithCategoryResponse>? = null
) : Response
