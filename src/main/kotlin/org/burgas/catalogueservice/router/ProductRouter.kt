package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.exception.ExceptionResponse
import org.burgas.catalogueservice.dto.product.ProductRequest
import org.burgas.catalogueservice.service.ProductService
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
class ProductRouter {

    private final val productService: ProductService

    constructor(productService: ProductService) {
        this.productService = productService
    }

    @Bean
    fun productRoutes() = coRouter {

        "/api/v1/products".nest {

            GET("") {
                ok().bodyValueAndAwait(productService.findAll())
            }

            GET("/by-id") {
                ok().bodyValueAndAwait(
                    productService.findById(
                        UUID.fromString(
                            it.queryParamOrNull("productId")
                        )
                    )
                )
            }

            POST("/create") {
                productService.create(it.awaitBody(ProductRequest::class))
                ok().buildAndAwait()
            }

            PUT("/update") {
                productService.update(it.awaitBody(ProductRequest::class))
                ok().buildAndAwait()
            }

            DELETE("/delete") {
                productService.delete(
                    UUID.fromString(
                        it.queryParamOrNull("productId")
                    )
                )
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