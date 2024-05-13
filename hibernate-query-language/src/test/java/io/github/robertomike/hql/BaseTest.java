package io.github.robertomike.hql;

import io.github.robertomike.hefesto.builders.BaseBuilder;
import io.github.robertomike.hefesto.configs.HefestoAutoconfiguration;
import io.github.robertomike.hql.configs.Config;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class BaseTest implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    protected static Session session;
    protected static SessionFactory sessionFactory;
    protected static EntityManager entityManager;

    /**
     * Gate keeper to prevent multiple Threads within the same routine
     */
    private static final Lock LOCK = new ReentrantLock();
    /**
     * volatile boolean to tell other threads, when unblocked, whether they should try attempt start-up.  Alternatively, could use AtomicBoolean.
     */
    private static volatile boolean started = false;

    @Override
    public void beforeAll(final ExtensionContext context) {
        // lock the access so only one Thread has access to it
        LOCK.lock();
        try {
            if (!started) {
                started = true;


                if (session != null) {
                    BaseBuilder.setSession(session);
                    return;
                }

                Config config = new Config();
                sessionFactory = config.sessionFactory();
                session = config.session(sessionFactory);
                entityManager = sessionFactory.createEntityManager();
                config.basicData(entityManager);

                new HefestoAutoconfiguration(entityManager);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // free the access
            LOCK.unlock();
        }
    }

    @Override
    public void close() {
        if (session != null) {
            session.close();
            sessionFactory.close();
        }
    }
}
