package io.github.robertomike.hefesto.actions

import io.github.robertomike.hefesto.enums.Sort

/**
 * Represents an ORDER BY clause in a query.
 * 
 * Specifies which field to sort by and the sort direction.
 * 
 * @property field the field name to order by (can include table alias, e.g., "user.createdAt")
 * @property sort the sort direction (ASC or DESC), defaults to ASC
 * 
 * Example:
 * ```kotlin
 * Order("name")                  // Ascending order by default
 * Order("createdAt", Sort.DESC)  // Descending order
 * ```
 */
data class Order(
    val field: String,
    val sort: Sort = Sort.ASC
)
