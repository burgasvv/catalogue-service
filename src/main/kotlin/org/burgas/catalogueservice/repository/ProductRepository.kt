package org.burgas.catalogueservice.repository

import kotlinx.coroutines.flow.Flow
import org.burgas.catalogueservice.entity.product.Product
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductRepository : CoroutineCrudRepository<Product, UUID> {

    fun findProductsByCategoryIdIn(categoryIds: MutableCollection<UUID>): Flow<Product>

    @Query(
        value = """
            select p.* from product p left join public.publication_product pp on p.id = pp.product_id
            where pp.publication_id = :publicationId
        """
    )
    suspend fun findProductsByPublicationId(publicationId: UUID): Flow<Product>
}