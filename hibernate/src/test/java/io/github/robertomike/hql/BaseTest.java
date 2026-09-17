package io.github.robertomike.hql;

import io.github.robertomike.commons.HibernateTestSupport;
import io.github.robertomike.hql.configs.Config;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class BaseTest implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    private static final HibernateTestSupport SUPPORT = new HibernateTestSupport(Config::new);

    protected static Session session;
    protected static SessionFactory sessionFactory;
    protected static EntityManager entityManager;

    @Override
    public void beforeAll(final ExtensionContext context) {
        SUPPORT.beforeAll();
        session = SUPPORT.session();
        sessionFactory = SUPPORT.sessionFactory();
        entityManager = SUPPORT.entityManager();
    }

    @Override
    public void close() {
        SUPPORT.close();
    }
}
