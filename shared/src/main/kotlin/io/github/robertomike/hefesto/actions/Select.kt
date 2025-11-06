package io.github.robertomike.hefesto.actions

import io.github.robertomike.hefesto.enums.SelectOperator

data class Select(
    val field: String,
    var alias: String? = null,
    var operator: SelectOperator? = null
) {
    constructor(field: String, alias: String) : this(field, alias, null)
    constructor(field: String, operator: SelectOperator) : this(field, null, operator)
}
