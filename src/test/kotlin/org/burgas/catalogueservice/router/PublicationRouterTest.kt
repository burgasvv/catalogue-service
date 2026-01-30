package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.catalogue.CatalogueRequest
import org.burgas.catalogueservice.dto.catalogue.CatalogueShortResponse
import org.burgas.catalogueservice.dto.publication.PublicationRequest
import org.burgas.catalogueservice.dto.publication.PublicationShortResponse
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
import java.time.LocalDate

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
class PublicationRouterTest(
    @Autowired
    private final val webTestClient: WebTestClient
) {
    @Test
    @Order(value = 1)
    @WithUserDetails(value = "burgasvv@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test create publication endpoint`() {
        val objectMapper = ObjectMapper()
        val catalogueRequest = CatalogueRequest(
            name = "Techno-Test",
            description = "Описание каталога Techno-Test"
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/api/v1/catalogues/create")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(catalogueRequest)
            .exchange()
            .returnResult()
        val cataloguesResult = this.webTestClient
            .get()
            .uri("/api/v1/catalogues")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val catalogueShortResponses =
            objectMapper.readValue<List<CatalogueShortResponse>>(cataloguesResult.responseBodyContent!!)
        val catalogueShortResponse = catalogueShortResponses.first { first -> first.name == "Techno-Test" }
        val publicationRequest = PublicationRequest(
            name = "Publication First",
            description = "Описание публикации Publication First",
            date = LocalDate.of(2026, 1, 30),
            catalogueId = catalogueShortResponse.id
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/api/v1/publications/create")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(publicationRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 2)
    @WithUserDetails(value = "burgasvv@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test update publication endpoint`() {
        val objectMapper = ObjectMapper()
        val publicationsResult = this.webTestClient
            .get()
            .uri("/api/v1/publications")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val publicationShortResponses =
            objectMapper.readValue<List<PublicationShortResponse>>(publicationsResult.responseBodyContent!!)
        val publicationShortResponse = publicationShortResponses.first { first -> first.name == "Publication First" }
        val publicationRequest = PublicationRequest(
            id = publicationShortResponse.id,
            name = "Publication First Test"
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .put()
            .uri("/api/v1/publications/update")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(publicationRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 3)
    fun `test find all publications endpoint`() {
        this.webTestClient
            .get()
            .uri("/api/v1/publications")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 4)
    fun `test find publication by id endpoint`() {
        val objectMapper = ObjectMapper()
        val publicationsResult = this.webTestClient
            .get()
            .uri("/api/v1/publications")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val publicationShortResponses =
            objectMapper.readValue<List<PublicationShortResponse>>(publicationsResult.responseBodyContent!!)
        val publicationShortResponse =
            publicationShortResponses.first { first -> first.name == "Publication First Test" }
        this.webTestClient
            .get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/publications/by-id")
                    .queryParam("publicationId", publicationShortResponse.id.toString())
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
    fun `test delete publication endpoint`() {
        val objectMapper = ObjectMapper()
        val publicationsResult = this.webTestClient
            .get()
            .uri("/api/v1/publications")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val publicationShortResponses =
            objectMapper.readValue<List<PublicationShortResponse>>(publicationsResult.responseBodyContent!!)
        val publicationShortResponse =
            publicationShortResponses.first { first -> first.name == "Publication First Test" }
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .delete()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/publications/delete")
                    .queryParam("publicationId", publicationShortResponse.id.toString())
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult()
        val cataloguesResult = this.webTestClient
            .get()
            .uri("/api/v1/catalogues")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val catalogueShortResponses =
            objectMapper.readValue<List<CatalogueShortResponse>>(cataloguesResult.responseBodyContent!!)
        val catalogueShortResponse = catalogueShortResponses.first { first -> first.name == "Techno-Test" }
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
            .returnResult()
    }
}