package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.builders.BaseBuilder
import io.github.robertomike.hefesto.enums.WhereOperator

/**
 * Represents an EXISTS or NOT EXISTS subquery condition.
 * 
 * Checks whether a subquery returns any rows.
 * 
 * @property exists true for EXISTS, false for NOT EXISTS
 * @property subQuery the subquery builder to execute
 * 
 * Example:
 * ```kotlin
 * // WHERE EXISTS (SELECT 1 FROM orders WHERE orders.userId = user.id)
 * WhereExist(true, Hefesto.make(Order::class.java)
 *     .whereRaw("orders.userId = user.id"))
 * 
 * // WHERE NOT EXISTS (SELECT 1 FROM payments WHERE payments.orderId = order.id)
 * WhereExist(false, Hefesto.make(Payment::class.java)
 *     .whereRaw("payments.orderId = order.id"))
 * ```
 */
data class WhereExist(
    var exists: Boolean = true,
    val subQuery: BaseBuilder<*, *, *, *, *, *, *, *>
) : BaseWhere() {

    constructor(subQuery: BaseBuilder<*, *, *, *, *, *, *, *>) : this(true, subQuery)

    constructor(subQuery: BaseBuilder<*, *, *, *, *, *, *, *>, whereOperator: WhereOperator) : this(true, subQuery) {
        this.whereOperation = whereOperator
    }

    constructor(exists: Boolean, subQuery: BaseBuilder<*, *, *, *, *, *, *, *>, whereOperator: WhereOperator) : this(exists, subQuery) {
        this.whereOperation = whereOperator
    }
}
