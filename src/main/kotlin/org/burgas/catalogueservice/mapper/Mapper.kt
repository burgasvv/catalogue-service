package org.burgas.catalogueservice.mapper

import org.burgas.catalogueservice.dto.Request
import org.burgas.catalogueservice.dto.Response
import org.burgas.catalogueservice.entity.Model

interface Mapper<in R : Request, M : Model, out S : Response, out F : Response> {

    suspend fun toEntity(request: R): M

    suspend fun toShortResponse(entity: M): S

    suspend fun toFullResponse(entity: M): F
}