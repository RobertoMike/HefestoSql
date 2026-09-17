package io.github.robertomike.hefesto.hql.constructors

import io.github.robertomike.hefesto.constructors.ConstructGroupBy

/**
 * HQL implementation of GROUP BY clause construction.
 * 
 * Converts GROUP BY definitions into HQL string-based grouping clauses.
 * Groups query results by specified fields, typically used with aggregate functions.
 * 
 * Example output: "group by user.status, user.role"
 */
class ConstructGroupByImplementation : ConstructGroupBy() {
    /**
     * Constructs the GROUP BY clause as an HQL string.
     *
     * @return the HQL GROUP BY clause string (e.g., "group by field1, field2"), or empty string if no fields
     */
    fun construct(): String {
        if (items.isEmpty()) {
            return ""
        }

        val groupBy = StringBuilder()

        items.forEach { value ->
            if (groupBy.isNotEmpty()) {
                groupBy.append(", ")
            }

            groupBy.append(value.field)
        }

        return "group by $groupBy"
    }
}
