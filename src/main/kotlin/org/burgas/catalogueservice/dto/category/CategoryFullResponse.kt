package org.burgas.catalogueservice.dto.category

import org.burgas.catalogueservice.dto.Response
import org.burgas.catalogueservice.dto.product.ProductShortResponse
import java.util.UUID

data class CategoryFullResponse(
    override val id: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val products: List<ProductShortResponse>? = null
) : Response
