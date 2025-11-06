package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.builders.BaseBuilder
import io.github.robertomike.hefesto.enums.WhereOperator

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
