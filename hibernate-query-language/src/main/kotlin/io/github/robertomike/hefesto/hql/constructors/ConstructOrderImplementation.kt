package io.github.robertomike.hefesto.hql.constructors

import io.github.robertomike.hefesto.actions.Order
import io.github.robertomike.hefesto.constructors.ConstructOrder

/**
 * HQL implementation of ORDER BY clause construction.
 * 
 * Converts ORDER BY definitions into HQL string-based ordering clauses.
 * Supports:
 * - Ascending and descending sort orders
 * - Multiple order criteria
 * - Field references with or without table aliases
 * 
 * Example output: "order by user.name ASC, user.createdAt DESC"
 */
class ConstructOrderImplementation : ConstructOrder() {
    /**
     * Constructs the ORDER BY clause as an HQL string.
     *
     * @return the HQL ORDER BY clause string (e.g., "order by field1 ASC, field2 DESC")
     */
    fun construct(): String {
        val ordersQuery = StringBuilder()

        items.forEach { value ->
            if (ordersQuery.isNotEmpty()) {
                ordersQuery.append(", ")
            }

            ordersQuery.append(apply(value))
        }

        if (items.isNotEmpty()) {
            ordersQuery.insert(0, "order by ")
        }

        return ordersQuery.toString()
    }

    fun apply(value: Order): String {
        return "${value.field} ${value.sort.name}"
    }
}
