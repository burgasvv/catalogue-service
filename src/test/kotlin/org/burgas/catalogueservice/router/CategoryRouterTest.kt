package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.category.CategoryRequest
import org.burgas.catalogueservice.dto.category.CategoryShortResponse
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
class CategoryRouterTest(
    @Autowired
    private final val webTestClient: WebTestClient
) {
    @Test
    @Order(value = 1)
    @WithUserDetails(value = "burgasvv@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test create category endpoint`() {
        val categoryRequest = CategoryRequest(
            name = "Оператива",
            description = "Описание продукта Оператива"
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/api/v1/categories/create")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(categoryRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 2)
    @WithUserDetails(value = "burgasvv@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test update category endpoint`() {
        val objectMapper = ObjectMapper()
        val categoriesResult = this.webTestClient
            .get()
            .uri("/api/v1/categories")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val categoryShortResponses =
            objectMapper.readValue<List<CategoryShortResponse>>(categoriesResult.responseBodyContent!!)
        val categoryShortResponse = categoryShortResponses.first { it.name == "Оператива" }
        val categoryRequest = CategoryRequest(
            id = categoryShortResponse.id,
            name = "Оперативная Память",
            description = "Описание продукта Оперативная Память"
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .put()
            .uri("/api/v1/categories/update")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(categoryRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 3)
    fun `test find all categories endpoint`() {
        this.webTestClient
            .get()
            .uri("/api/v1/categories")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 4)
    fun `test find by id category endpoint`() {
        val objectMapper = ObjectMapper()
        val categoriesResult = this.webTestClient
            .get()
            .uri("/api/v1/categories")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val categoryShortResponses =
            objectMapper.readValue<List<CategoryShortResponse>>(categoriesResult.responseBodyContent!!)
        val categoryShortResponse = categoryShortResponses.first { it.name == "Оперативная Память" }
        this.webTestClient
            .get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/categories/by-id")
                    .queryParam("categoryId", categoryShortResponse.id.toString())
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
    fun `test delete category endpoint`() {
        val objectMapper = ObjectMapper()
        val categoriesResult = this.webTestClient
            .get()
            .uri("/api/v1/categories")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val categoryShortResponses =
            objectMapper.readValue<List<CategoryShortResponse>>(categoriesResult.responseBodyContent!!)
        val categoryShortResponse = categoryShortResponses.first { it.name == "Оперативная Память" }
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .delete()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/categories/delete")
                    .queryParam("categoryId", categoryShortResponse.id.toString())
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }
}