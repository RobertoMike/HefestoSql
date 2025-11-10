package io.github.robertomike.hefesto.models

import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.*

/**
 * Extended base interface for Hibernate entities that provides default table name resolution.
 * 
 * This interface extends BaseModel and provides automatic table name derivation from
 * the entity class name or JPA annotations (@Table, @Entity).
 * 
 * Example:
 * ```java
 * @Entity
 * @Table(name = "users")
 * public class User implements HibernateModel {
 *     // getTable() returns "User"
 *     // getOriginalTable() returns "users"
 * }
 * ```
 * 
 * The interface provides two methods:
 * - getTable(): Returns simple class name for internal alias generation
 * - getOriginalTable(): Returns actual database table name from annotations
 */
interface HibernateModel : BaseModel {
    /**
     * Returns the simple name of the implementing class as the table identifier.
     * This is used internally for query construction and alias generation.
     *
     * @return the simple class name (e.g., "User" for User.class)
     */
    override fun getTable(): String {
        return javaClass.simpleName
    }

    /**
     * Returns the actual database table name from JPA annotations.
     * 
     * Resolution order:
     * 1. @Table(name = "...") annotation value
     * 2. @Entity(name = "...") annotation value  
     * 3. Simple class name in lowercase
     *
     * @return the database table name
     */
    fun getOriginalTable(): String {
        val table = javaClass.getAnnotation(Table::class.java)

        if (table != null) {
            return table.name
        }

        val entity = javaClass.getAnnotation(Entity::class.java)

        if (entity != null && entity.name.isNotEmpty()) {
            return entity.name
        }

        return getTable().lowercase(Locale.ROOT)
    }
}
