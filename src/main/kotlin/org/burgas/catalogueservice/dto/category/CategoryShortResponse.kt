package org.burgas.catalogueservice.dto.category

import org.burgas.catalogueservice.dto.Response
import java.util.UUID

data class CategoryShortResponse(
    override val id: UUID? = null,
    val name: String? = null,
    val description: String? = null
) : Response
