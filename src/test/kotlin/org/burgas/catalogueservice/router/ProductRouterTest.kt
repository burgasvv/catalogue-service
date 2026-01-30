package org.burgas.catalogueservice.router

import org.burgas.catalogueservice.dto.category.CategoryRequest
import org.burgas.catalogueservice.dto.category.CategoryShortResponse
import org.burgas.catalogueservice.dto.product.ProductRequest
import org.burgas.catalogueservice.dto.product.ProductShortResponse
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
class ProductRouterTest(
    @Autowired
    private final val webTestClient: WebTestClient
) {
    @Test
    @Order(value = 1)
    @WithUserDetails(value = "burgasvv@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test create product endpoint`() {
        val objectMapper = ObjectMapper()
        val categoryRequest = CategoryRequest(
            name = "Оперативная Память",
            description = "Описание категории Оперативная Память"
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/api/v1/categories/create")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(categoryRequest)
            .exchange()
            .returnResult()
        val categoriesResult = this.webTestClient
            .get()
            .uri("/api/v1/categories")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val categoryShortResponses =
            objectMapper.readValue<List<CategoryShortResponse>>(categoriesResult.responseBodyContent!!)
        val categoryShortResponse = categoryShortResponses.first { it.name == "Оперативная Память" }
        val productRequest = ProductRequest(
            name = "DDR5 r-2",
            categoryId = categoryShortResponse.id,
            description = "Описание товара DDR5 r-2",
            price = 80500.50
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/api/v1/products/create")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(productRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 2)
    @WithUserDetails(value = "burgasvv@gmail.com", userDetailsServiceBeanName = "identityDetailsService")
    fun `test update product endpoint`() {
        val objectMapper = ObjectMapper()
        val productsResult = this.webTestClient
            .get()
            .uri("/api/v1/products")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val productShortResponses =
            objectMapper.readValue<List<ProductShortResponse>>(productsResult.responseBodyContent!!)
        val productShortResponse = productShortResponses.first { first -> first.name == "DDR5 r-2" }
        val productRequest = ProductRequest(
            id = productShortResponse.id,
            name = "DDR5 r-2 Test",
            description = "Описание товара DDR5 r-2 Test"
        )
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .put()
            .uri("/api/v1/products/update")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(productRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 3)
    fun `test get all products endpoint`() {
        this.webTestClient
            .get()
            .uri("/api/v1/products")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult()
    }

    @Test
    @Order(value = 4)
    fun `test get product by id endpoint`() {
        val objectMapper = ObjectMapper()
        val productsResult = this.webTestClient
            .get()
            .uri("/api/v1/products")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val productShortResponses =
            objectMapper.readValue<List<ProductShortResponse>>(productsResult.responseBodyContent!!)
        val productShortResponse = productShortResponses.first { first -> first.name == "DDR5 r-2 Test" }
        this.webTestClient
            .get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/products/by-id")
                    .queryParam("productId", productShortResponse.id.toString())
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
    fun `test delete product endpoint`() {
        val objectMapper = ObjectMapper()
        val productsResult = this.webTestClient
            .get()
            .uri("/api/v1/products")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult()
        val productShortResponses =
            objectMapper.readValue<List<ProductShortResponse>>(productsResult.responseBodyContent!!)
        val productShortResponse = productShortResponses.first { first -> first.name == "DDR5 r-2 Test" }
        this.webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .delete()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/products/delete")
                    .queryParam("productId", productShortResponse.id.toString())
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .returnResult()
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
            .returnResult()
    }
}