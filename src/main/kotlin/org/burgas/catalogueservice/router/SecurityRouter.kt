package org.burgas.catalogueservice.router

import kotlinx.coroutines.reactor.awaitSingle
import org.burgas.catalogueservice.dto.exception.ExceptionResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import reactor.core.publisher.Mono

@Configuration
class SecurityRouter {

    @Bean
    fun securityRoutes() = coRouter {

        "/api/v1/security".nest {

            GET("/csrf-token") {
                val csrfTokenMono = it.exchange().getAttribute<Mono<CsrfToken>>(CsrfToken::class.java.name)
                val csrfToken = csrfTokenMono!!.awaitSingle()
                ok().bodyValueAndAwait(csrfToken)
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