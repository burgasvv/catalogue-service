package org.burgas.catalogueservice.repository

import org.burgas.catalogueservice.entity.publicationProduct.PublicationProduct
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PublicationProductRepository : CoroutineCrudRepository<PublicationProduct, Pair<UUID, UUID>>