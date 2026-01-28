package org.burgas.catalogueservice.mapper

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.burgas.catalogueservice.dto.catalogue.CatalogueFullResponse
import org.burgas.catalogueservice.dto.catalogue.CatalogueRequest
import org.burgas.catalogueservice.dto.catalogue.CatalogueShortResponse
import org.burgas.catalogueservice.entity.catalogue.Catalogue
import org.burgas.catalogueservice.repository.CatalogueRepository
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CatalogueMapper : Mapper<CatalogueRequest, Catalogue, CatalogueShortResponse, CatalogueFullResponse> {

    final var catalogueRepository: CatalogueRepository
    private final val publicationMapperObjectFactory: ObjectFactory<PublicationMapper>

    constructor(
        catalogueRepository: CatalogueRepository,
        publicationMapperObjectFactory: ObjectFactory<PublicationMapper>
    ) {
        this.catalogueRepository = catalogueRepository
        this.publicationMapperObjectFactory = publicationMapperObjectFactory
    }

    private fun getPublicationMapper(): PublicationMapper = this.publicationMapperObjectFactory.`object`

    override suspend fun toEntity(request: CatalogueRequest): Catalogue {
        val catalogue = this.catalogueRepository.findById(request.id ?: UUID.randomUUID())
        return if (catalogue != null) {
            Catalogue().apply {
                this.id = catalogue.id
                this.name = request.name ?: catalogue.name
                this.description = request.description ?: catalogue.description
            }
        } else {
            Catalogue().apply {
                this.name = request.name ?: throw IllegalArgumentException("Catalogue name is null")
                this.description =
                    request.description ?: throw IllegalArgumentException("Catalogue description is null")
            }
        }
    }

    override suspend fun toShortResponse(entity: Catalogue): CatalogueShortResponse {
        return CatalogueShortResponse(
            id = entity.id,
            name = entity.name,
            description = entity.description
        )
    }

    override suspend fun toFullResponse(entity: Catalogue): CatalogueFullResponse {
        return CatalogueFullResponse(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            publications = this.getPublicationMapper().publicationRepository.findPublicationsByCatalogueIdIn(
                mutableListOf(entity.id)
            )
                .map { publication -> this.getPublicationMapper().toShortResponse(publication) }
                .toList()
        )
    }
}