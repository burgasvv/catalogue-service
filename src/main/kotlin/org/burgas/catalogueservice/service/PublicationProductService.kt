package org.burgas.catalogueservice.service

import org.burgas.catalogueservice.entity.publicationProduct.PublicationProduct
import org.burgas.catalogueservice.repository.PublicationProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
class PublicationProductService {

    private final val publicationProductRepository: PublicationProductRepository

    constructor(publicationProductRepository: PublicationProductRepository) {
        this.publicationProductRepository = publicationProductRepository
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    suspend fun add(publicationProducts: List<PublicationProduct>) {
        publicationProducts.forEach { publicationProduct ->
            this.publicationProductRepository.save(publicationProduct)
        }
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    suspend fun remove(publicationProducts: List<PublicationProduct>) {
        publicationProducts.forEach { publicationProduct ->
            this.publicationProductRepository.deletePublicationProductByPublicationIdAndProductId(
                publicationProduct.publicationId, publicationProduct.productId
            )
        }
    }
}