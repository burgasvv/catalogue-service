package org.burgas.catalogueservice.dto.product

import org.burgas.catalogueservice.dto.Response
import org.burgas.catalogueservice.dto.category.CategoryShortResponse
import java.util.UUID

data class ProductWithCategoryResponse(
    override val id: UUID? = null,
    val name: String? = null,
    val category: CategoryShortResponse? = null,
    val description: String? = null,
    val price: Double? = null
) : Response
