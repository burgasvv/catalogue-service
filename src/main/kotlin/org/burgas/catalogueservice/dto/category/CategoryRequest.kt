package org.burgas.catalogueservice.dto.category

import org.burgas.catalogueservice.dto.Request
import java.util.UUID

data class CategoryRequest(
    override val id: UUID? = null,
    val name: String? = null,
    val description: String? = null
) : Request
