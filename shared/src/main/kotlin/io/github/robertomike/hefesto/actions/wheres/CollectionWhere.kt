package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.enums.WhereOperator

/**
 * Represents a grouped collection of WHERE conditions wrapped in parentheses.
 * 
 * Used to create complex WHERE clauses with proper precedence using AND/OR logic.
 * 
 * @property wheres the list of WHERE conditions to group together
 * 
 * Example:
 * ```kotlin
 * // (status = 'active' OR status = 'pending') AND age > 18
 * CollectionWhere(listOf(
 *     Where("status", Operator.EQUAL, "active"),
 *     Where("status", Operator.EQUAL, "pending").apply { whereOperation = WhereOperator.OR }
 * ))
 * Where("age", Operator.GREATER, 18)
 * ```
 */
data class CollectionWhere(
    /**
     * The WHERE conditions that will be grouped inside parentheses
     */
    val wheres: List<out BaseWhere>
) : BaseWhere() {

    /**
     * @param wheres the wheres that will be used inside the parenthesis
     * @param whereOperation the operator that will be used
     */
    constructor(wheres: List<out BaseWhere>, whereOperation: WhereOperator) : this(wheres) {
        this.whereOperation = whereOperation
    }
}
