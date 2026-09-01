package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.JoinFetch
import jakarta.persistence.criteria.Root

class ConstructJoinFetch : Construct<JoinFetch>() {
    fun construct(root: Root<*>) {
        items.forEach { value ->
            root.fetch<Any, Any>(value.relationship, value.joinType)
        }
    }
}
