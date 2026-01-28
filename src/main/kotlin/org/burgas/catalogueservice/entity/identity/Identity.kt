package org.burgas.catalogueservice.entity.identity

import org.burgas.catalogueservice.entity.Model
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "identity", schema = "public")
class Identity : Model {

    @Id
    @Column(value = "id")
    override lateinit var id: UUID

    @Column(value = "authority")
    lateinit var authority: Authority

    @Column(value = "username")
    lateinit var username: String

    @Column(value = "password")
    lateinit var password: String

    @Column(value = "email")
    lateinit var email: String

    @Column(value = "enabled")
    var enabled: Boolean = true

    @Column(value = "firstname")
    lateinit var firstname: String

    @Column(value = "lastname")
    lateinit var lastname: String

    @Column(value = "patronymic")
    lateinit var patronymic: String
}