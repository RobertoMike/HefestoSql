package io.github.robertomike.hefesto.constructors

import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Root

class ConstructJoinImplementation<T> : ConstructJoin() {
    val joins: MutableMap<String, Join<*, *>> = HashMap()

    fun construct(root: Root<*>) {
        items.forEach { value ->
            val joinType = when (value.joinOperator) {
                io.github.robertomike.hefesto.enums.JoinOperator.INNER -> JoinType.INNER
                io.github.robertomike.hefesto.enums.JoinOperator.LEFT -> JoinType.LEFT
                io.github.robertomike.hefesto.enums.JoinOperator.RIGHT -> JoinType.RIGHT
            }

            var name = value.table
            val hasAlias = value.fieldJoin != null

            if (hasAlias) {
                name = value.fieldJoin!!
            }

            val join: Join<*, *> = root.join<Any, Any>(value.table, joinType)
            joins[name] = join
        }
    }
}
