package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.enums.WhereOperator

/**
 * Represents a standard WHERE condition in a query.
 * 
 * Compares a field against a value using a comparison operator.
 * 
 * @property field the field name to filter on (can include table alias, e.g., "user.name")
 * @property operator the comparison operator (EQUAL, GREATER, LIKE, IN, etc.)
 * @property value the value to compare against (can be null for IS_NULL/IS_NOT_NULL)
 * 
 * Example:
 * ```kotlin
 * Where("name", Operator.EQUAL, "John")           // name = 'John'
 * Where("age", Operator.GREATER, 18)              // age > 18
 * Where("email", Operator.LIKE, "%@example.com")  // email LIKE '%@example.com'
 * Where("status", Operator.IN, listOf("active", "pending"))  // status IN ('active', 'pending')
 * Where("deletedAt", Operator.IS_NULL)            // deletedAt IS NULL
 * ```
 */
open class Where(
    val field: String,
    open var operator: Operator = Operator.EQUAL,
    open var value: Any? = null
) : BaseWhere() {

    constructor(field: String, operator: Operator) : this(field, operator, null)

    constructor(field: String, value: Any?) : this(field, Operator.EQUAL, value)

    constructor(field: String, operator: Operator, value: Any?, whereOperator: WhereOperator) : this(field, operator, value) {
        this.whereOperation = whereOperator
    }

    companion object {
        @JvmStatic
        fun make(field: String, value: Any?, whereOperator: WhereOperator): Where {
            return Where(field, Operator.EQUAL, value, whereOperator)
        }

        @JvmStatic
        fun make(field: String, operator: Operator, whereOperator: WhereOperator): Where {
            return Where(field, operator, null, whereOperator)
        }
    }
}
