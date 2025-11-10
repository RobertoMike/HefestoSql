package io.github.robertomike.hefesto.actions

/**
 * Represents a GROUP BY clause field in a query.
 * 
 * Groups query results by the specified field, typically used with aggregate functions.
 * 
 * @property field the field name to group by (can include table alias, e.g., "user.status")
 * 
 * Example:
 * ```kotlin
 * GroupBy("status")          // Group by status field
 * GroupBy("user.department") // Group by department on joined user
 * ```
 */
data class GroupBy(val field: String)
