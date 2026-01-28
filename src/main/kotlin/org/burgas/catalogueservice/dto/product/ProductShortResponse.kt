package org.burgas.catalogueservice.dto.product

import org.burgas.catalogueservice.dto.Response
import java.util.*

data class ProductShortResponse(
    override val id: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null
) : Response
