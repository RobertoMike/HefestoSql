package io.github.robertomike.hefesto.configs

import io.github.robertomike.hefesto.builders.BaseBuilder
import jakarta.persistence.EntityManager
import org.hibernate.Session

class HefestoAutoconfiguration(entityManager: EntityManager) {
    init {
        BaseBuilder.setSession(entityManager.unwrap(Session::class.java))
    }
}
