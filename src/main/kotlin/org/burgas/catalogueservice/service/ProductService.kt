package org.burgas.catalogueservice.service

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.burgas.catalogueservice.dto.product.ProductFullResponse
import org.burgas.catalogueservice.dto.product.ProductRequest
import org.burgas.catalogueservice.dto.product.ProductShortResponse
import org.burgas.catalogueservice.entity.product.Product
import org.burgas.catalogueservice.mapper.ProductMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
class ProductService : AsyncCrudService<ProductRequest, Product, ProductShortResponse, ProductFullResponse> {

    private final val productMapper: ProductMapper

    constructor(productMapper: ProductMapper) {
        this.productMapper = productMapper
    }

    override suspend fun findEntity(id: UUID): Product {
        return this.productMapper.productRepository.findById(id) ?: throw IllegalArgumentException("Product not found")
    }

    override suspend fun findById(id: UUID): ProductFullResponse {
        return this.productMapper.toFullResponse(this.findEntity(id))
    }

    override suspend fun findAll(): List<ProductShortResponse> {
        return this.productMapper.productRepository.findAll()
            .map { product -> this.productMapper.toShortResponse(product) }
            .toList()
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun create(request: ProductRequest) {
        val product = this.productMapper.toEntity(request)
        this.productMapper.productRepository.save(product)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun update(request: ProductRequest) {
        if (request.id == null) {
            throw IllegalArgumentException("Product id is null")
        }
        val product = this.productMapper.toEntity(request)
        this.productMapper.productRepository.save(product)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun delete(id: UUID) {
        val product = this.findEntity(id)
        this.productMapper.productRepository.delete(product)
    }
}