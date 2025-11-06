package io.github.robertomike.hefesto.utils

import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Path

object HibernateUtils {
    const val DOT_REGEX = "\\."

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
