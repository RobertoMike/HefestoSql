package io.github.robertomike.hefesto.constructors

import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Root

class ConstructGroupByImplementation : ConstructGroupBy() {
    fun construct(cr: CriteriaQuery<*>, root: Root<*>) {
        if (items.isEmpty()) {
            return
        }

        cr.groupBy(*items.map { value -> root.get<Any>(value.field) }.toTypedArray())
    }
}
