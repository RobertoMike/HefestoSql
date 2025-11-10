package io.github.robertomike.hefesto.utils

import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Path

/**
 * Utility object for Hibernate Criteria API operations.
 * Provides helper methods for field path resolution and navigation.
 */
object HibernateUtils {
    /** Regular expression pattern for splitting nested field paths */
    const val DOT_REGEX = "\\."

    /**
     * Resolves a field path from a From element, supporting nested property access.
     * 
     * This method handles both simple fields and nested paths using dot notation.
     * For example, "user.address.city" will navigate through the object graph to get the city field.
     *
     * @param from the From element (Root or Join) to start navigation from
     * @param field the field name or nested path (e.g., "name" or "user.address.city")
     * @param <T> the type of the field
     * @return the Path representing the field
     */
    fun <T> getFieldFrom(from: From<*, *>, field: String): Path<T> {
        if (!field.contains(".")) {
            return from.get(field)
        }

        val split = field.split(DOT_REGEX.toRegex())
        var path: Path<T>? = null

        for (o in split) {
            path = if (path == null) {
                from.get(o)
            } else {
                path.get(o)
            }
        }

        return path!!
    }
}
