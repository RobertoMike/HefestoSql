package io.github.robertomike.configs;

import io.github.robertomike.builders.BaseBuilder;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.hibernate.Session;

public class HefestoAutoconfiguration {
    public HefestoAutoconfiguration(EntityManager entityManager) {
        BaseBuilder.setSession(entityManager.unwrap(Session.class));
    }
}
