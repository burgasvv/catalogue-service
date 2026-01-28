package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.exception.ExceptionResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class SecurityRouter {

    @Bean
    fun securityRoutes() = router {

        GET("/api/v1/security/csrf-token") { request ->
            val csrfTokenMono: Mono<CsrfToken> = request.exchange()
                .getAttribute(CsrfToken::class.java.getName()) ?: throw IllegalArgumentException("Csrf token is null")
            csrfTokenMono.flatMap { csrfToken -> ServerResponse.ok().bodyValue(csrfToken) }
        }

        onError<Exception> { throwable, _ ->
            val exceptionResponse = ExceptionResponse(
                status = HttpStatus.BAD_REQUEST.name,
                code = HttpStatus.BAD_REQUEST.ordinal,
                message = throwable.localizedMessage
            )
            badRequest().bodyValue(exceptionResponse)
        }
    }
}