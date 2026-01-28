package org.burgas.catalogueservice.repository

import kotlinx.coroutines.flow.Flow
import org.burgas.catalogueservice.entity.publication.Publication
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PublicationRepository : CoroutineCrudRepository<Publication, UUID> {

    fun findPublicationsByCatalogueIdIn(catalogueIds: MutableCollection<UUID>): Flow<Publication>

    @Query(
        value = """
            select p.* from publication p left join public.publication_product pp on p.id = pp.publication_id
            where pp.product_id = :productId
        """
    )
    fun findPublicationsByProductId(productId: UUID): Flow<Publication>
}