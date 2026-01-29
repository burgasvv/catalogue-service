package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.identity.IdentityRequest
import org.burgas.catalogueservice.dto.identity.IdentityShortResponse
import org.burgas.catalogueservice.entity.identity.Authority
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
class IdentityRouterTest(
    @Autowired
    private final val webTestClient: WebTestClient
) {
    @Test
    @Order(value = 1)
    fun `test create identity endpoint`() {
        val identityRequest = IdentityRequest(
            authority = Authority.ADMIN,
            username = "admin",
            password = "admin",
            email = "admin@gmail.com",
            enabled = true,
            firstname = "Admin",
            lastname = "Admin",
            patronymic = "Admin"
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/api/v1/identities/create")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(identityRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 2)
    @WithUserDetails(value = "admin@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test update identity endpoint`() {
        val objectMapper = ObjectMapper()
        val identitiesResult = this.webTestClient
            .get()
            .uri("/api/v1/identities")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val identityShortResponses =
            objectMapper.readValue<List<IdentityShortResponse>>(identitiesResult.responseBodyContent!!)
        val identityShortResponse = identityShortResponses.first { it.email == "admin@gmail.com" }
        val identityRequest = IdentityRequest(
            id = identityShortResponse.id,
            username = "admin test"
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .put()
            .uri("/api/v1/identities/update")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(identityRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 3)
    @WithUserDetails(value = "admin@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test find all identities endpoint`() {
        this.webTestClient
            .get()
            .uri("/api/v1/identities")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 4)
    @WithUserDetails(value = "admin@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test find by id identity endpoint`() {
        val objectMapper = ObjectMapper()
        val identitiesResult = this.webTestClient
            .get()
            .uri("/api/v1/identities")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val identityShortResponses =
            objectMapper.readValue<List<IdentityShortResponse>>(identitiesResult.responseBodyContent!!)
        val identityShortResponse = identityShortResponses.first { it.email == "admin@gmail.com" }
        this.webTestClient
            .get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/identities/by-id")
                    .queryParam("identityId", identityShortResponse.id.toString())
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 5)
    @WithUserDetails(value = "admin@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test delete identity endpoint`() {
        val objectMapper = ObjectMapper()
        val identitiesResult = this.webTestClient
            .get()
            .uri("/api/v1/identities")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val identityShortResponses =
            objectMapper.readValue<List<IdentityShortResponse>>(identitiesResult.responseBodyContent!!)
        val identityShortResponse = identityShortResponses.first { it.email == "admin@gmail.com" }
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .delete()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/identities/delete")
                    .queryParam("identityId", identityShortResponse.id.toString())
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
    }
}