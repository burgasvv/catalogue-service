package org.burgas.catalogueservice.entity.product

import org.burgas.catalogueservice.entity.Model
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "product", schema = "public")
class Product : Model {

    @Id
    @Column(value = "id")
    override lateinit var id: UUID

    @Column(value = "name")
    lateinit var name: String

    @Column(value = "category_id")
    var categoryId: UUID? = null

    @Column(value = "description")
    lateinit var description: String

    @Column(value = "price")
    var price: Double = 0.0
}