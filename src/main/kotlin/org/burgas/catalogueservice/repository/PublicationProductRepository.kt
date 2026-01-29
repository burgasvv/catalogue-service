package org.burgas.catalogueservice.repository

import org.burgas.catalogueservice.entity.publicationProduct.PublicationProduct
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PublicationProductRepository : CoroutineCrudRepository<PublicationProduct, UUID> {

    @Modifying
    @Query(
        value = """
            delete from publication_product pp where pp.publication_id = :publicationId and pp.product_id = :productId
        """
    )
    suspend fun deletePublicationProductByPublicationIdAndProductId(
        publicationId: UUID,
        productId: UUID
    )
}