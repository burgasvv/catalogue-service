package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.catalogue.CatalogueRequest
import org.burgas.catalogueservice.dto.exception.ExceptionResponse
import org.burgas.catalogueservice.service.CatalogueService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.queryParamOrNull
import java.util.UUID

@Configuration
class CatalogueRouter {

    private final val catalogueService: CatalogueService

    constructor(catalogueService: CatalogueService) {
        this.catalogueService = catalogueService
    }

    @Bean
    fun catalogueRoutes() = coRouter {

        "/api/v1/catalogues".nest {

            GET("") {
                ok().bodyValueAndAwait(catalogueService.findAll())
            }

            GET("/by-id") {
                ok().bodyValueAndAwait(
                    UUID.fromString(
                        it.queryParamOrNull("catalogueId")
                    )
                )
            }

            POST("/create") {
                catalogueService.create(it.awaitBody(CatalogueRequest::class))
                ok().buildAndAwait()
            }

            PUT("/update") {
                catalogueService.update(it.awaitBody(CatalogueRequest::class))
                ok().buildAndAwait()
            }

            DELETE("/delete") {
                catalogueService.delete(
                    UUID.fromString(
                        it.queryParamOrNull("catalogueId")
                    )
                )
                ok().buildAndAwait()
            }

            onError<Exception> { throwable, _ ->
                val exceptionResponse = ExceptionResponse(
                    status = HttpStatus.BAD_REQUEST.name,
                    code = HttpStatus.BAD_REQUEST.ordinal,
                    message = throwable.localizedMessage
                )
                badRequest().bodyValueAndAwait(exceptionResponse)
            }
        }
    }
}