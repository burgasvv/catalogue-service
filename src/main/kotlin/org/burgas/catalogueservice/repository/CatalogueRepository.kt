package org.burgas.catalogueservice.repository

import org.burgas.catalogueservice.entity.catalogue.Catalogue
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CatalogueRepository : CoroutineCrudRepository<Catalogue, UUID>