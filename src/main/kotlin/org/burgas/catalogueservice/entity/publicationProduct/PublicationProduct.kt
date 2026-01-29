package org.burgas.catalogueservice.entity.publicationProduct

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table(name = "publication_product", schema = "public")
class PublicationProduct {

    lateinit var publicationId: UUID

    lateinit var productId: UUID
}