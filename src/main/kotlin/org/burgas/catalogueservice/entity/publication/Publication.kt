package org.burgas.catalogueservice.entity.publication

import org.burgas.catalogueservice.entity.Model
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.UUID

@Table(name = "publication", schema = "public")
class Publication : Model {

    @Id
    @Column(value = "id")
    override lateinit var id: UUID

    @Column(value = "name")
    lateinit var name: String

    @Column(value = "description")
    lateinit var description: String

    @Column(value = "date")
    lateinit var date: LocalDate

    @Column(value = "catalogue_id")
    var catalogueId: UUID? = null
}