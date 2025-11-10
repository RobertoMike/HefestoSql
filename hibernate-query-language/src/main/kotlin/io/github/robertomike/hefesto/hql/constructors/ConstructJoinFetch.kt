package io.github.robertomike.hefesto.hql.constructors

import io.github.robertomike.hefesto.actions.JoinFetch
import io.github.robertomike.hefesto.hql.builders.Hefesto
import io.github.robertomike.hefesto.constructors.Construct

class ConstructJoinFetch : Construct<JoinFetch>() {
    fun construct(builder: Hefesto<*>): String {
        val joinQuery = StringBuilder()

        items.forEach { value ->
            if (joinQuery.isNotEmpty()) {
                joinQuery.append(" ")
            }

            joinQuery.append(apply(builder, value))
        }

        return joinQuery.toString()
    }

    fun apply(builder: Hefesto<*>, join: JoinFetch): String {
        return "${join.joinType} join fetch ${if (join.nested) "" else "${builder.acronymTable}."}${join.relationship}${if (join.alias != null) " as ${join.alias}" else ""}"
    }
}
