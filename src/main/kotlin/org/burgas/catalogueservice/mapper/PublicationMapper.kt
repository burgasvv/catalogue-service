package org.burgas.catalogueservice.mapper

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.burgas.catalogueservice.dto.catalogue.CatalogueShortResponse
import org.burgas.catalogueservice.dto.publication.PublicationFullResponse
import org.burgas.catalogueservice.dto.publication.PublicationRequest
import org.burgas.catalogueservice.dto.publication.PublicationShortResponse
import org.burgas.catalogueservice.dto.publication.PublicationWithCatalogueResponse
import org.burgas.catalogueservice.entity.catalogue.Catalogue
import org.burgas.catalogueservice.entity.publication.Publication
import org.burgas.catalogueservice.repository.PublicationRepository
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter
import java.util.UUID

@Component
class PublicationMapper : Mapper<PublicationRequest, Publication, PublicationShortResponse, PublicationFullResponse> {

    final val publicationRepository: PublicationRepository
    private final val catalogueMapperObjectFactory: ObjectFactory<CatalogueMapper>
    private final val productMapperObjectFactory: ObjectFactory<ProductMapper>

    constructor(
        publicationRepository: PublicationRepository,
        catalogueMapperObjectFactory: ObjectFactory<CatalogueMapper>,
        productMapperObjectFactory: ObjectFactory<ProductMapper>
    ) {
        this.publicationRepository = publicationRepository
        this.catalogueMapperObjectFactory = catalogueMapperObjectFactory
        this.productMapperObjectFactory = productMapperObjectFactory
    }

    private fun getCatalogueMapper(): CatalogueMapper = this.catalogueMapperObjectFactory.`object`

    private fun getProductMapper(): ProductMapper = this.productMapperObjectFactory.`object`

    override suspend fun toEntity(request: PublicationRequest): Publication {
        val publication = this.publicationRepository.findById(request.id ?: UUID.randomUUID())
        return if (publication != null) {
            Publication().apply {
                this.id = publication.id
                this.name = request.name ?: publication.name
                this.description = request.description ?: publication.description
                this.date = request.date ?: publication.date
                this.catalogueId = request.catalogueId ?: publication.catalogueId
            }
        } else {
            Publication().apply {
                this.name = request.name ?: throw IllegalArgumentException("Publication name is null")
                this.description =
                    request.description ?: throw IllegalArgumentException("Publication description is null")
                this.date = request.date ?: throw IllegalArgumentException("Publication date is null")
                this.catalogueId =
                    request.catalogueId ?: throw IllegalArgumentException("Publication categoryId is null")
            }
        }
    }

    override suspend fun toShortResponse(entity: Publication): PublicationShortResponse {
        return PublicationShortResponse(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            date = entity.date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        )
    }

    suspend fun toPublicationWithCatalogueResponse(entity: Publication): PublicationWithCatalogueResponse {
        return PublicationWithCatalogueResponse(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            date = entity.date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
            catalogue = this.getCatalogueShortResponse(entity)
        )
    }

    override suspend fun toFullResponse(entity: Publication): PublicationFullResponse {
        return PublicationFullResponse(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            date = entity.date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
            catalogue = this.getCatalogueShortResponse(entity),
            products = this.getProductMapper().productRepository.findProductsByPublicationId(entity.id)
                .map { product -> this.getProductMapper().toProductWithCategoryResponse(product) }
                .toList()
        )
    }

    private suspend fun getCatalogueShortResponse(entity: Publication): CatalogueShortResponse? {
        var catalogue: Catalogue? = null
        if (entity.catalogueId != null) {
            catalogue = this.getCatalogueMapper().catalogueRepository.findById(entity.catalogueId!!)
        }
        return if (catalogue != null) this.getCatalogueMapper().toShortResponse(catalogue) else null
    }
}