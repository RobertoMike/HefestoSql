package io.github.robertomike.hefesto.configs

import io.github.robertomike.hefesto.builders.BaseBuilder
import jakarta.persistence.EntityManager
import org.hibernate.Session

/**
 * Auto-configuration class for Spring Boot integration.
 * Automatically configures HefestoSQL when an EntityManager is available in the application context.
 * 
 * This class extracts the Hibernate Session from the EntityManager and sets it as the
 * default session for all Hefesto query builders, eliminating the need for manual configuration.
 * 
 * Usage:
 * ```java
 * // Spring Boot 2 or 3 - No configuration needed!
 * // Just use Hefesto directly in your services:
 * 
 * @Service
 * public class UserService {
 *     public List<User> getActiveUsers() {
 *         return Hefesto.make(User.class)
 *             .where("status", "ACTIVE")
 *             .get();
 *     }
 * }
 * ```
 * 
 * For non-Spring applications, manually configure the session:
 * ```java
 * Hefesto.setSession(hibernateSession);
 * ```
 *
 * @param entityManager the JPA EntityManager from Spring context
 */
class HefestoAutoconfiguration(entityManager: EntityManager) {
    init {
        BaseBuilder.setSession(entityManager.unwrap(Session::class.java))
    }
}
