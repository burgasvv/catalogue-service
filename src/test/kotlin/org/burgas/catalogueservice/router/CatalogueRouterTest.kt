package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.catalogue.CatalogueRequest
import org.burgas.catalogueservice.dto.catalogue.CatalogueShortResponse
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
class CatalogueRouterTest(
    @Autowired
    private final val webTestClient: WebTestClient
) {
    @Test
    @Order(value = 1)
    @WithUserDetails(value = "burgasvv@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test create catalogue endpoint`() {
        val catalogueRequest = CatalogueRequest(
            name = "Techno-Land",
            description = "Описание каталога Techno-Land"
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/api/v1/catalogues/create")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(catalogueRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 2)
    @WithUserDetails(value = "burgasvv@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test update catalogue endpoint`() {
        val objectMapper = ObjectMapper()
        val cataloguesResult = this.webTestClient
            .get()
            .uri("/api/v1/catalogues")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val catalogueShortResponses =
            objectMapper.readValue<List<CatalogueShortResponse>>(cataloguesResult.responseBodyContent!!)
        val catalogueShortResponse = catalogueShortResponses.first { first -> first.name == "Techno-Land" }
        val catalogueRequest = CatalogueRequest(
            id = catalogueShortResponse.id,
            name = "Techno-Land Test",
            description = "Описание каталога Techno-Land Test"
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .put()
            .uri("/api/v1/catalogues/update")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(catalogueRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 3)
    fun `test find all catalogues endpoint`() {
        this.webTestClient
            .get()
            .uri("/api/v1/catalogues")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 4)
    fun `test find catalogue by id endpoint`() {
        val objectMapper = ObjectMapper()
        val cataloguesResult = this.webTestClient
            .get()
            .uri("/api/v1/catalogues")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val catalogueShortResponses =
            objectMapper.readValue<List<CatalogueShortResponse>>(cataloguesResult.responseBodyContent!!)
        val catalogueShortResponse = catalogueShortResponses.first { first -> first.name == "Techno-Land Test" }
        this.webTestClient
            .get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/catalogues/by-id")
                    .queryParam("catalogueId", catalogueShortResponse.id.toString())
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 5)
    @WithUserDetails(value = "burgasvv@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test delete catalogue endpoint`() {
        val objectMapper = ObjectMapper()
        val cataloguesResult = this.webTestClient
            .get()
            .uri("/api/v1/catalogues")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val catalogueShortResponses =
            objectMapper.readValue<List<CatalogueShortResponse>>(cataloguesResult.responseBodyContent!!)
        val catalogueShortResponse = catalogueShortResponses.first { first -> first.name == "Techno-Land Test" }
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .delete()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/catalogues/delete")
                    .queryParam("catalogueId", catalogueShortResponse.id.toString())
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }
}