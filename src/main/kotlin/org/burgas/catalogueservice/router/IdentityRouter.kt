package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.exception.ExceptionResponse
import org.burgas.catalogueservice.dto.identity.IdentityRequest
import org.burgas.catalogueservice.entity.identity.IdentityDetails
import org.burgas.catalogueservice.service.IdentityService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.reactive.function.server.*
import java.util.*

@Configuration
class IdentityRouter {

    private final val identityService: IdentityService

    constructor(identityService: IdentityService) {
        this.identityService = identityService
    }

    @Bean
    fun identityRoutes() = coRouter {

        "/api/v1/identities".nest {

            filter { request, function ->
                if (
                    request.path().equals("/api/v1/identities/by-id", false) ||
                    request.path().equals("/api/v1/identities/delete", false)
                ) {
                    val authentication = request.awaitPrincipal() as Authentication
                    if (authentication.isAuthenticated) {
                        val identityDetails = authentication.principal as IdentityDetails
                        val identityId = UUID.fromString(
                            request.queryParamOrNull("identityId")
                                ?: throw IllegalArgumentException("identityId param is null")
                        )

                        if (identityId == identityDetails.identity.id) {
                            function(request)

                        } else {
                            throw IllegalArgumentException("Identity not authorized")
                        }

                    } else {
                        throw IllegalArgumentException("Identity not authenticated")
                    }
                } else if (
                    request.path().equals("/api/v1/identities/update", false) ||
                    request.path().equals("/api/v1/identities/change-password", false)
                ) {
                    val authentication = request.awaitPrincipal() as Authentication
                    if (authentication.isAuthenticated) {
                        val identityDetails = authentication.principal as IdentityDetails
                        val identityRequest = request.awaitBody(IdentityRequest::class)
                        val identityId = identityRequest.id
                            ?: throw IllegalArgumentException("Identity id from request is null")

                        if (identityId == identityDetails.identity.id) {
                            request.attributes()["identityRequest"] = identityRequest
                            function(request)
                        } else {
                            throw IllegalArgumentException("Identity not authorized")
                        }

                    } else {
                        throw IllegalArgumentException("Identity not authenticated")
                    }
                }
                function(request)
            }

            GET("") {
                status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(identityService.findAll())
            }

            GET("/by-id") {
                status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(
                        identityService.findById(
                            UUID.fromString(
                                it.queryParamOrNull("identityId")
                            )
                        )
                    )
            }

            POST("/create") {
                identityService.create(it.awaitBody(IdentityRequest::class))
                ok().buildAndAwait()
            }

            PUT("/update") {
                val identityRequest = it.attribute("identityRequest")
                    .orElseThrow { throw IllegalArgumentException("Identity Request not found") } as IdentityRequest
                identityService.update(identityRequest)
                ok().buildAndAwait()
            }

            DELETE("/delete") {
                identityService.delete(UUID.fromString(it.queryParamOrNull("identityId")))
                ok().buildAndAwait()
            }

            PUT("/change-password") {
                val identityRequest = it.attribute("identityRequest")
                    .orElseThrow { throw IllegalArgumentException("Identity Request not found") } as IdentityRequest
                identityService.changePassword(identityRequest)
                ok().buildAndAwait()
            }

            PUT("/change-status") {
                identityService.changeStatus(it.awaitBody(IdentityRequest::class))
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