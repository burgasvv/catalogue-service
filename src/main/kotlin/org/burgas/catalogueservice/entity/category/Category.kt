package org.burgas.catalogueservice.entity.category

import org.burgas.catalogueservice.entity.Model
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "category", schema = "public")
class Category : Model {

    @Id
    @Column(value = "id")
    override lateinit var id: UUID

    @Column(value = "name")
    lateinit var name: String

    @Column(value = "description")
    lateinit var description: String
}