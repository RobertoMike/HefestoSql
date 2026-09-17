package io.github.robertomike.commons;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Implemented by each suite's own {@code Config} class (Criteria and HQL each have
 * their own, pointing at different databases/entities) so both can share one
 * {@link HibernateTestSupport} bootstrap.
 */
public interface TestDbConfig {
    SessionFactory sessionFactory();

    Session session(SessionFactory sessionFactory);

    void basicData(EntityManager entityManager);
}
