package org.burgas.catalogueservice.entity.catalogue

import org.burgas.catalogueservice.entity.Model
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "catalogue", schema = "public")
class Catalogue : Model {

    @Id
    @Column(value = "id")
    override lateinit var id: UUID

    @Column(value = "name")
    lateinit var name: String

    @Column(value = "description")
    lateinit var description: String
}