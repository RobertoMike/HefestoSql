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

        // Deep join support: handle nested relationships like "user.address.city"
        if (table.contains(".")) {
            return applyDeepJoin(fatherAcronym, table, join)
        }

        // Simple join (no dot notation)
        table = "$fatherAcronym.$table"

        val acronym = join.getAcronym()
        val basic = "${join.joinOperator.operator} join $table $acronym"

        if (join.fieldReference != null && join.fieldJoin != null) {
            var fieldReference = join.fieldReference

            fieldReference = if (fieldReference!!.contains(".")) fieldReference else "$fatherAcronym.$fieldReference"

            return "${join.joinOperator.operator} join ${join.table} $acronym on $acronym.${join.fieldJoin} = $fieldReference"
        }

        return basic
    }

    /**
     * Handles deep joins with dot notation (e.g., "user.address.city")
     * Creates intermediate joins for each level of the relationship.
     */
    private fun applyDeepJoin(fatherAcronym: String, path: String, join: Join): String {
        val parts = path.split(".")
        val joins = StringBuilder()
        var currentAcronym = fatherAcronym

        // Create joins for each level except the last
        for (i in 0 until parts.size - 1) {
            val relationName = parts[i]
            val intermediateAcronym = "${fatherAcronym}_${parts.subList(0, i + 1).joinToString("_")}"
            
            if (joins.isNotEmpty()) {
                joins.append(" ")
            }
            
            joins.append("${join.joinOperator.operator} join $currentAcronym.$relationName $intermediateAcronym")
            currentAcronym = intermediateAcronym
        }

        // Add the final join with the specified acronym
        val lastRelation = parts.last()
        val finalAcronym = join.getAcronym()
        
        if (joins.isNotEmpty()) {
            joins.append(" ")
        }
        
        joins.append("${join.joinOperator.operator} join $currentAcronym.$lastRelation $finalAcronym")

        return joins.toString()
    }
}
