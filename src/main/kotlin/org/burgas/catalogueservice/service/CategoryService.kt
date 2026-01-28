package org.burgas.catalogueservice.service

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.burgas.catalogueservice.dto.category.CategoryFullResponse
import org.burgas.catalogueservice.dto.category.CategoryRequest
import org.burgas.catalogueservice.dto.category.CategoryShortResponse
import org.burgas.catalogueservice.entity.category.Category
import org.burgas.catalogueservice.mapper.CategoryMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
class CategoryService : AsyncCrudService<CategoryRequest, Category, CategoryShortResponse, CategoryFullResponse> {

    private final val categoryMapper: CategoryMapper

    constructor(categoryMapper: CategoryMapper) {
        this.categoryMapper = categoryMapper
    }

    override suspend fun findEntity(id: UUID): Category {
        return this.categoryMapper.categoryRepository.findById(id) ?: throw IllegalArgumentException("Identity not found")
    }

    override suspend fun findById(id: UUID): CategoryFullResponse {
        return this.categoryMapper.toFullResponse(this.findEntity(id))
    }

    override suspend fun findAll(): List<CategoryShortResponse> {
        return this.categoryMapper.categoryRepository.findAll()
            .map { category -> this.categoryMapper.toShortResponse(category) }
            .toList()
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun create(request: CategoryRequest) {
        val category = this.categoryMapper.toEntity(request)
        this.categoryMapper.categoryRepository.save(category)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun update(request: CategoryRequest) {
        if (request.id == null) {
            throw IllegalArgumentException("Category id is null")
        }
        val category = this.categoryMapper.toEntity(request)
        this.categoryMapper.categoryRepository.save(category)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override suspend fun delete(id: UUID) {
        val category = this.findEntity(id)
        this.categoryMapper.categoryRepository.delete(category)
    }
}