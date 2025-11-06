package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.enums.WhereOperator

class WhereRaw(
    val query: String,
    whereOperator: WhereOperator = WhereOperator.AND
) : BaseWhere() {
    init {
        this.whereOperation = whereOperator
    }
}
