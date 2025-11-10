package io.github.robertomike.hefesto.benchmarks;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Base class for Hefesto benchmarks.
 * Sets up Hibernate session with H2 in-memory database.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public abstract class BaseBenchmark {
    
    protected SessionFactory sessionFactory;
    protected Session session;
    
    @Setup(Level.Trial)
    public void setupTrial() {
        Configuration configuration = new Configuration();
        
        // H2 in-memory database configuration
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:benchmark_db;DB_CLOSE_DELAY=-1");
        configuration.setProperty("hibernate.connection.username", "sa");
        configuration.setProperty("hibernate.connection.password", "");
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "false");
        configuration.setProperty("hibernate.format_sql", "false");
        
        // Register entities
        registerEntities(configuration);
        
        sessionFactory = configuration.buildSessionFactory();
        
        // Populate test data
        session = sessionFactory.openSession();
        session.beginTransaction();
        populateTestData(session);
        session.getTransaction().commit();
    }
    
    @Setup(Level.Iteration)
    public void setupIteration() {
        if (session == null || !session.isOpen()) {
            session = sessionFactory.openSession();
        }
    }
    
    @TearDown(Level.Iteration)
    public void tearDownIteration() {
        if (session != null && session.isOpen()) {
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            session.close();
        }
    }
    
    @TearDown(Level.Trial)
    public void tearDownTrial() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
    
    /**
     * Register Hibernate entities for the benchmark.
     */
    protected abstract void registerEntities(Configuration configuration);
    
    /**
     * Populate test data for the benchmark.
     */
    protected abstract void populateTestData(Session session);
}
