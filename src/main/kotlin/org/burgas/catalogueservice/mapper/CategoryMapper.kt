package org.burgas.catalogueservice.mapper

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.burgas.catalogueservice.dto.category.CategoryFullResponse
import org.burgas.catalogueservice.dto.category.CategoryRequest
import org.burgas.catalogueservice.dto.category.CategoryShortResponse
import org.burgas.catalogueservice.entity.category.Category
import org.burgas.catalogueservice.repository.CategoryRepository
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class CategoryMapper : Mapper<CategoryRequest, Category, CategoryShortResponse, CategoryFullResponse> {

    final val categoryRepository: CategoryRepository
    private final val productMapperObjectFactory: ObjectFactory<ProductMapper>

    constructor(categoryRepository: CategoryRepository, productMapperObjectFactory: ObjectFactory<ProductMapper>) {
        this.categoryRepository = categoryRepository
        this.productMapperObjectFactory = productMapperObjectFactory
    }

    private fun getProductMapper(): ProductMapper = this.productMapperObjectFactory.`object`

    override suspend fun toEntity(request: CategoryRequest): Category {
        val category = this.categoryRepository.findById(request.id ?: UUID.randomUUID())
        return if (category != null) {
            Category().apply {
                this.id = category.id
                this.name = request.name ?: category.name
                this.description = request.description ?: category.description
            }
        } else {
            Category().apply {
                this.name = request.name ?: throw IllegalArgumentException("Category name is null")
                this.description = request.description ?: throw IllegalArgumentException("Category description is null")
            }
        }
    }

    override suspend fun toShortResponse(entity: Category): CategoryShortResponse {
        return CategoryShortResponse(
            id = entity.id,
            name = entity.name,
            description = entity.description
        )
    }

    override suspend fun toFullResponse(entity: Category): CategoryFullResponse {
        return CategoryFullResponse(
            id = entity.id,
            name = entity.description,
            description = entity.description,
            products = this.getProductMapper().productRepository.findProductsByCategoryIdIn(mutableListOf(entity.id))
                .map { product -> this.getProductMapper().toShortResponse(product) }
                .toList()
        )
    }
}