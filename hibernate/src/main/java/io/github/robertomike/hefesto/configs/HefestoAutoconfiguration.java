package io.github.robertomike.hefesto.configs;

import io.github.robertomike.hefesto.builders.BaseBuilder;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;

public class HefestoAutoconfiguration {
    public HefestoAutoconfiguration(EntityManager entityManager) {
        BaseBuilder.setSession(entityManager.unwrap(Session.class));
    }
}
