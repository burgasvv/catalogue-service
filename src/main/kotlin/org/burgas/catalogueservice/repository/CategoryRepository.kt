package org.burgas.catalogueservice.repository

import org.burgas.catalogueservice.entity.category.Category
import org.springframework.context.annotation.Configuration
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

@Configuration
interface CategoryRepository : CoroutineCrudRepository<Category, UUID>