package org.burgas.catalogueservice.service

import org.burgas.catalogueservice.dto.Request
import org.burgas.catalogueservice.dto.Response
import org.burgas.catalogueservice.entity.Model
import java.util.UUID

interface AsyncCrudService<in R : Request, M : Model, S : Response, F : Response> {

    suspend fun findEntity(id: UUID): M

    suspend fun findById(id: UUID): F

    suspend fun findAll(): List<S>

    suspend fun create(request: R)

    suspend fun update(request: R)

    suspend fun delete(id: UUID)
}