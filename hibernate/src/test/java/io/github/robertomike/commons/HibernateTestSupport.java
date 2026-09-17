package io.github.robertomike.commons;

import io.github.robertomike.hefesto.builders.BaseBuilder;
import io.github.robertomike.hefesto.configs.HefestoAutoconfiguration;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Shared JUnit bootstrap for a single test lineage (e.g. Criteria or HQL). Each
 * lineage's {@code BaseTest} owns one static instance of this class, so the
 * SessionFactory it builds is started once per lineage and independently from
 * the other lineage sharing the same JVM/test task.
 */
public final class HibernateTestSupport {
    private final Lock lock = new ReentrantLock();
    private final Supplier<TestDbConfig> configFactory;

    private volatile boolean started = false;
    private Session session;
    private SessionFactory sessionFactory;
    private EntityManager entityManager;

    public HibernateTestSupport(Supplier<TestDbConfig> configFactory) {
        this.configFactory = configFactory;
    }

    public void beforeAll() {
        lock.lock();
        try {
            if (!started) {
                started = true;

                TestDbConfig config = configFactory.get();
                sessionFactory = config.sessionFactory();
                session = config.session(sessionFactory);
                entityManager = sessionFactory.createEntityManager();
                config.basicData(entityManager);

                new HefestoAutoconfiguration(entityManager);
            }

            // Re-assert this lineage's session as the active global session on every
            // beforeAll: the other lineage sharing this JVM/test task may have
            // overwritten BaseBuilder's global session since this lineage last ran.
            BaseBuilder.setSession(session);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        if (session != null) {
            session.close();
            sessionFactory.close();
        }
    }

    public Session session() {
        return session;
    }

    public SessionFactory sessionFactory() {
        return sessionFactory;
    }

    public EntityManager entityManager() {
        return entityManager;
    }
}
