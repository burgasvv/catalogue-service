package org.burgas.catalogueservice.dto.product

import org.burgas.catalogueservice.dto.Request
import java.util.UUID

data class ProductRequest(
    override val id: UUID? = null,
    val name: String? = null,
    val categoryId: UUID? = null,
    val description: String? = null,
    val price: Double? = null
) : Request
