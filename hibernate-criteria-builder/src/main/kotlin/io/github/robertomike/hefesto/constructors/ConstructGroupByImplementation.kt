package io.github.robertomike.hefesto.constructors

import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Root

/**
 * Criteria API implementation of GROUP BY clause construction.
 * 
 * Converts GROUP BY definitions into JPA Criteria API groupBy expressions.
 * Groups query results by specified fields, typically used with aggregate functions.
 * 
 * Example:
 * ```kotlin
 * query
 *     .select("status", "count")
 *     .selectCount("id")
 *     .groupBy("status")
 * ```
 */
class ConstructGroupByImplementation : ConstructGroupBy() {
    /**
     * Constructs and applies GROUP BY clauses to the CriteriaQuery.
     * Maps field names to root entity expressions and groups results.
     *
     * @param cr the CriteriaQuery to apply grouping to
     * @param root the root entity
     */
    fun construct(cr: CriteriaQuery<*>, root: Root<*>) {
        if (items.isEmpty()) {
            return
        }

        cr.groupBy(*items.map { value -> root.get<Any>(value.field) }.toTypedArray())
    }
}
