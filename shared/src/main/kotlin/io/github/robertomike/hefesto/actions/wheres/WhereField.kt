package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.enums.WhereOperator

/**
 * Represents a WHERE condition that compares two fields instead of a field and a value.
 * 
 * This is useful for:
 * - Comparing fields within the same entity
 * - Comparing fields across joined entities
 * - Subquery correlations (comparing parent entity field with subquery entity field)
 * 
 * @property field the first field name to compare (can include table alias or join reference)
 * @property operator the comparison operator (EQUAL, GREATER, LESS, etc.)
 * @property secondField the second field name to compare against
 * 
 * Example:
 * ```kotlin
 * // Compare two fields on same entity
 * WhereField("startDate", Operator.LESS, "endDate")  // WHERE startDate < endDate
 * 
 * // Compare fields across joins
 * WhereField("user.createdAt", Operator.GREATER, "order.createdAt")
 * 
 * // Subquery correlation
 * WhereField("userId", Operator.EQUAL, "id")  // WHERE subquery.userId = parent.id
 * ```
 */
class WhereField(
    field: String,
    operator: Operator = Operator.EQUAL,
    val secondField: String
) : Where(field, operator, null) {

    /**
     * Constructor with explicit whereOperation for AND/OR chaining.
     *
     * @param field the first field name
     * @param operator the comparison operator
     * @param secondField the second field name
     * @param whereOperation the logical operator (AND/OR) to combine with previous conditions
     */
    constructor(
        field: String,
        operator: Operator,
        secondField: String,
        whereOperation: WhereOperator
    ) : this(field, operator, secondField) {
        this.whereOperation = whereOperation
    }

    /**
     * Constructor with default EQUAL operator.
     *
     * @param field the first field name
     * @param secondField the second field name
     */
    constructor(field: String, secondField: String) : this(field, Operator.EQUAL, secondField)
}
