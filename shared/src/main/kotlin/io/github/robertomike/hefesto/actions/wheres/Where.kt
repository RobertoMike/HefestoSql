package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.enums.WhereOperator

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
