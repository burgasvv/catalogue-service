package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.exception.ExceptionResponse
import org.burgas.catalogueservice.entity.publicationProduct.PublicationProduct
import org.burgas.catalogueservice.service.PublicationProductService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class PublicationProductRouter {

    private final val publicationProductService: PublicationProductService

    constructor(publicationProductService: PublicationProductService) {
        this.publicationProductService = publicationProductService
    }

    @Bean
    fun publicationProductRoutes() = coRouter {

        "/api/v1/publication-product".nest {

            POST("/add") {
                val publicationProducts = it.awaitBody<List<PublicationProduct>>()
                publicationProductService.add(publicationProducts)
                ok().buildAndAwait()
            }

            DELETE("/remove") {
                val publicationProducts = it.awaitBody<List<PublicationProduct>>()
                publicationProductService.remove(publicationProducts)
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