package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.enums.WhereOperator

class WhereRaw @JvmOverloads constructor(
    val query: String,
    whereOperator: WhereOperator = WhereOperator.AND
) : BaseWhere() {
    init {
        this.whereOperation = whereOperator
    }
}
