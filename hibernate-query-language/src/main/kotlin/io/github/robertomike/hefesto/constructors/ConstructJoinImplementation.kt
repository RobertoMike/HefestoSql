package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.Join
import io.github.robertomike.hefesto.builders.Hefesto

class ConstructJoinImplementation : ConstructJoin() {
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

    fun apply(builder: Hefesto<*>, join: Join): String {
        val fatherAcronym = builder.acronymTable
        var table = join.table

        if (!table.contains(".")) {
            table = "$fatherAcronym.$table"
        }

        val acronym = join.getAcronym()
        val basic = "${join.joinOperator.operator} join $table $acronym"

        if (join.fieldReference != null && join.fieldJoin != null) {
            var fieldReference = join.fieldReference

            fieldReference = if (fieldReference!!.contains(".")) fieldReference else "$fatherAcronym.$fieldReference"

            return "${join.joinOperator.operator} join ${join.table} $acronym on $acronym.${join.fieldJoin} = $fieldReference"
        }

        return basic
    }
}
