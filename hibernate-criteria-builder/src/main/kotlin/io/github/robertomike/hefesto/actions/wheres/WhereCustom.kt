package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.enums.WhereOperator
import jakarta.persistence.criteria.*

class WhereCustom(val custom: Custom, whereOperator: WhereOperator = WhereOperator.AND) : BaseWhere() {
    init {
        this.whereOperation = whereOperator
    }

    fun interface Custom {
        fun call(
            cb: CriteriaBuilder,
            cr: CriteriaQuery<*>,
            root: Root<*>,
            joins: @JvmSuppressWildcards Map<String, Join<*, *>>,
            parentRoot: Root<*>
        ): Predicate
    }
}
