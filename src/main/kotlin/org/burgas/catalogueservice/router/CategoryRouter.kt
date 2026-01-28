package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.category.CategoryRequest
import org.burgas.catalogueservice.dto.exception.ExceptionResponse
import org.burgas.catalogueservice.service.CategoryService
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
class CategoryRouter {

    private final val categoryService: CategoryService

    constructor(categoryService: CategoryService) {
        this.categoryService = categoryService
    }

    @Bean
    fun categoryRoutes() = coRouter {

        "/api/v1/categories".nest {

            GET("") {
                ok().bodyValueAndAwait(categoryService.findAll())
            }

            GET("/by-id") {
                ok().bodyValueAndAwait(
                    categoryService.findById(
                        UUID.fromString(
                            it.queryParamOrNull("categoryId")
                        )
                    )
                )
            }

            POST("/create") {
                categoryService.create(it.awaitBody(CategoryRequest::class))
                ok().buildAndAwait()
            }

            PUT("/update") {
                categoryService.update(it.awaitBody(CategoryRequest::class))
                ok().buildAndAwait()
            }

            DELETE("/delete") {
                categoryService.delete(
                    UUID.fromString(
                        it.queryParamOrNull("categoryId")
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