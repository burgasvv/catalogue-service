package org.burgas.catalogueservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

@SpringBootApplication
class CatalogueServiceApplication

fun main(args: Array<String>) {
    runApplication<CatalogueServiceApplication>(*args)
}