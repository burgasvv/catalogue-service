package org.burgas.catalogueservice.service

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.burgas.catalogueservice.dto.catalogue.CatalogueFullResponse
import org.burgas.catalogueservice.dto.catalogue.CatalogueRequest
import org.burgas.catalogueservice.dto.catalogue.CatalogueShortResponse
import org.burgas.catalogueservice.entity.catalogue.Catalogue
import org.burgas.catalogueservice.mapper.CatalogueMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
class CatalogueService : AsyncCrudService<CatalogueRequest, Catalogue, CatalogueShortResponse, CatalogueFullResponse> {

    private final val catalogueMapper: CatalogueMapper

    constructor(catalogueMapper: CatalogueMapper) {
        this.catalogueMapper = catalogueMapper
    }

    override suspend fun findEntity(id: UUID): Catalogue {
        return this.catalogueMapper.catalogueRepository.findById(id) ?: throw IllegalArgumentException("Catalogue not found")
    }

    override suspend fun findById(id: UUID): CatalogueFullResponse {
        return this.catalogueMapper.toFullResponse(this.findEntity(id))
    }

    override suspend fun findAll(): List<CatalogueShortResponse> {
        return this.catalogueMapper.catalogueRepository.findAll()
            .map { catalogue -> this.catalogueMapper.toShortResponse(catalogue) }
            .toList()
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun create(request: CatalogueRequest) {
        val catalogue = this.catalogueMapper.toEntity(request)
        this.catalogueMapper.catalogueRepository.save(catalogue)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun update(request: CatalogueRequest) {
        if (request.id == null) {
            throw IllegalArgumentException("Catalogue id is null")
        }
        val catalogue = this.catalogueMapper.toEntity(request)
        this.catalogueMapper.catalogueRepository.save(catalogue)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun delete(id: UUID) {
        val catalogue = this.findEntity(id)
        this.catalogueMapper.catalogueRepository.delete(catalogue)
    }
}