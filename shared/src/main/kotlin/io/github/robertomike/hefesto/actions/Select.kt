package io.github.robertomike.hefesto.actions

import io.github.robertomike.hefesto.enums.SelectOperator

/**
 * Represents a SELECT clause field in a query.
 * 
 * Can select a simple field, apply an aggregate function, or specify an alias.
 * 
 * @property field the field name to select (can include table alias, e.g., "user.name")
 * @property alias optional alias for the selected field in the result set
 * @property operator optional aggregate function to apply (COUNT, SUM, AVG, MIN, MAX)
 * 
 * Example:
 * ```kotlin
 * Select("name")                              // Simple field selection
 * Select("name", "userName")                  // With alias
 * Select("id", SelectOperator.COUNT)          // With aggregate function
 * Select("price", "totalPrice", SelectOperator.SUM)  // With both
 * ```
 */
data class Select(
    val field: String,
    var alias: String? = null,
    var operator: SelectOperator? = null
) {
    constructor(field: String, alias: String) : this(field, alias, null)
    constructor(field: String, operator: SelectOperator) : this(field, null, operator)
}
