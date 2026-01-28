package org.burgas.catalogueservice.mapper

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.burgas.catalogueservice.dto.category.CategoryShortResponse
import org.burgas.catalogueservice.dto.product.ProductFullResponse
import org.burgas.catalogueservice.dto.product.ProductRequest
import org.burgas.catalogueservice.dto.product.ProductShortResponse
import org.burgas.catalogueservice.dto.product.ProductWithCategoryResponse
import org.burgas.catalogueservice.entity.category.Category
import org.burgas.catalogueservice.entity.product.Product
import org.burgas.catalogueservice.repository.ProductRepository
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class ProductMapper : Mapper<ProductRequest, Product, ProductShortResponse, ProductFullResponse> {

    final val productRepository: ProductRepository
    private final val categoryMapperObjectFactory: ObjectFactory<CategoryMapper>
    private final val publicationMapperObjectFactory: ObjectFactory<PublicationMapper>

    constructor(
        productRepository: ProductRepository,
        categoryMapperObjectFactory: ObjectFactory<CategoryMapper>,
        publicationMapperObjectFactory1: ObjectFactory<PublicationMapper>
    ) {
        this.productRepository = productRepository
        this.categoryMapperObjectFactory = categoryMapperObjectFactory
        this.publicationMapperObjectFactory = publicationMapperObjectFactory1
    }

    private fun getCategoryMapper(): CategoryMapper = this.categoryMapperObjectFactory.`object`

    private fun getPublicationMapper(): PublicationMapper = this.publicationMapperObjectFactory.`object`

    override suspend fun toEntity(request: ProductRequest): Product {
        val product = this.productRepository.findById(request.id ?: UUID.randomUUID())
        return if (product != null) {
            Product().apply {
                this.id = product.id
                this.name = request.name ?: product.name
                this.categoryId = request.categoryId ?: product.categoryId
                this.description = request.description ?: product.description
                this.price = request.price ?: product.price
            }
        } else {
            Product().apply {
                this.name = request.name ?: throw IllegalArgumentException("Product name is null")
                this.categoryId = request.categoryId ?: throw IllegalArgumentException("Product categoryId is null")
                this.description = request.description ?: throw IllegalArgumentException("Product description is null")
                this.price = request.price ?: throw IllegalArgumentException("Product price is null")
            }
        }
    }

    override suspend fun toShortResponse(entity: Product): ProductShortResponse {
        return ProductShortResponse(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            price = entity.price
        )
    }

    suspend fun toProductWithCategoryResponse(entity: Product): ProductWithCategoryResponse {
        return ProductWithCategoryResponse(
            id = entity.id,
            name = entity.name,
            category = this.getCategoryShortResponse(entity),
            description = entity.description,
            price = entity.price
        )
    }

    override suspend fun toFullResponse(entity: Product): ProductFullResponse {
        return ProductFullResponse(
            id = entity.id,
            name = entity.name,
            category = this.getCategoryShortResponse(entity),
            description = entity.description,
            price = entity.price,
            publications = this.getPublicationMapper().publicationRepository.findPublicationsByProductId(entity.id)
                .map { publication -> this.getPublicationMapper().toPublicationWithCatalogueResponse(publication) }
                .toList()
        )
    }

    private suspend fun getCategoryShortResponse(entity: Product): CategoryShortResponse? {
        var category: Category? = null
        if (entity.categoryId != null) {
            category = this.getCategoryMapper().categoryRepository.findById(entity.categoryId!!)
        }
        return if (category != null) this.getCategoryMapper().toShortResponse(category) else null
    }
}