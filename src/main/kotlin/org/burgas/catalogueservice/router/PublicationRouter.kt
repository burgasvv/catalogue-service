package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.exception.ExceptionResponse
import org.burgas.catalogueservice.dto.publication.PublicationRequest
import org.burgas.catalogueservice.service.PublicationService
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
class PublicationRouter {

    private final val publicationService: PublicationService

    constructor(publicationService: PublicationService) {
        this.publicationService = publicationService
    }

    @Bean
    fun publicationRoutes() = coRouter {

        "/api/v1/publications".nest {

            GET("") {
                ok().bodyValueAndAwait(publicationService.findAll())
            }

            GET("/by-id") {
                ok().bodyValueAndAwait(
                    publicationService.findById(
                        UUID.fromString(it.queryParamOrNull("publicationId"))
                    )
                )
            }

            POST("/create") {
                publicationService.create(it.awaitBody(PublicationRequest::class))
                ok().buildAndAwait()
            }

            PUT("/update") {
                publicationService.update(it.awaitBody(PublicationRequest::class))
                ok().buildAndAwait()
            }

            DELETE("/delete") {
                publicationService.delete(UUID.fromString(it.queryParamOrNull("publicationId")))
                ok().buildAndAwait()
            }

            onError<Exception> { throwable, _ ->
                val exceptionResponse = ExceptionResponse(
                    status = HttpStatus.BAD_REQUEST.name,
                    code = HttpStatus.BAD_REQUEST.value(),
                    message = throwable.localizedMessage
                )
                badRequest().bodyValueAndAwait(exceptionResponse)
            }
        }
    }
}