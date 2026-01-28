package org.burgas.catalogueservice.service

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.burgas.catalogueservice.dto.publication.PublicationFullResponse
import org.burgas.catalogueservice.dto.publication.PublicationRequest
import org.burgas.catalogueservice.dto.publication.PublicationShortResponse
import org.burgas.catalogueservice.entity.publication.Publication
import org.burgas.catalogueservice.mapper.PublicationMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
class PublicationService : AsyncCrudService<PublicationRequest, Publication, PublicationShortResponse, PublicationFullResponse> {

    private final val publicationMapper: PublicationMapper

    constructor(publicationMapper: PublicationMapper) {
        this.publicationMapper = publicationMapper
    }

    override suspend fun findEntity(id: UUID): Publication {
        return this.publicationMapper.publicationRepository.findById(id) ?: throw IllegalArgumentException("Publication not found")
    }

    override suspend fun findById(id: UUID): PublicationFullResponse {
        return this.publicationMapper.toFullResponse(this.findEntity(id))
    }

    override suspend fun findAll(): List<PublicationShortResponse> {
        return this.publicationMapper.publicationRepository.findAll()
            .map { publication -> this.publicationMapper.toShortResponse(publication) }
            .toList()
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun create(request: PublicationRequest) {
        val publication = this.publicationMapper.toEntity(request)
        this.publicationMapper.publicationRepository.save(publication)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun update(request: PublicationRequest) {
        if (request.id == null) {
            throw IllegalArgumentException("Publication id is null")
        }
        val publication = this.publicationMapper.toEntity(request)
        this.publicationMapper.publicationRepository.save(publication)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun delete(id: UUID) {
        val publication = this.findEntity(id)
        this.publicationMapper.publicationRepository.delete(publication)
    }
}