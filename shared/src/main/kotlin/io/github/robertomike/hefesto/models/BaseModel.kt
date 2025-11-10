package io.github.robertomike.hefesto.models

/**
 * Base interface for all entity models used with HefestoSql.
 * 
 * All JPA entities used with Hefesto builders must implement this interface
 * to provide the table name for query construction.
 */
interface BaseModel {
    /**
     * Gets the table name for this entity.
     * Used by Hefesto to build queries with correct table references.
     *
     * @return the table name as a string (typically the entity class simple name)
     */
    fun getTable(): String
}
