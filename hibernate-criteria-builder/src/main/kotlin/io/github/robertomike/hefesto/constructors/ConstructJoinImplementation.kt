package io.github.robertomike.hefesto.constructors

import jakarta.persistence.criteria.*

class ConstructJoinImplementation<T> : ConstructJoin() {
    val joins: MutableMap<String, Join<*, *>> = HashMap()

    fun construct(root: Root<*>) {
        items.forEach { joinDef ->
            processJoin(root, joinDef)
        }
    }

    /**
     * Processes a join definition and its nested deep joins recursively
     * 
     * @param from the From element (Root or Join) to join from
     * @param joinDef the join definition to process
     */
    private fun processJoin(
        from: From<*, *>,
        joinDef: io.github.robertomike.hefesto.actions.Join
    ) {
        val joinType = when (joinDef.joinOperator) {
            io.github.robertomike.hefesto.enums.JoinOperator.INNER -> JoinType.INNER
            io.github.robertomike.hefesto.enums.JoinOperator.LEFT -> JoinType.LEFT
            io.github.robertomike.hefesto.enums.JoinOperator.RIGHT -> JoinType.RIGHT
        }

        // Determine the alias for this join
        val alias = joinDef.alias ?: joinDef.table

        // Create the join based on whether it's a custom join or relationship join
        val join: Join<*, *> = if (joinDef.isCustomJoin()) {
            // Custom join with explicit conditions
            // This would require more complex handling with CriteriaBuilder
            // For now, we'll use relationship join and log a warning
            // TODO: Implement custom join with on() conditions
            from.join<Any, Any>(joinDef.table, joinType)
        } else {
            // Relationship join (standard JPA relationship)
            from.join<Any, Any>(joinDef.table, joinType)
        }

        // Store the join with its alias
        joins[alias] = join

        // Process nested deep joins recursively
        if (joinDef.hasDeepJoins()) {
            joinDef.deepJoins.forEach { nestedJoin ->
                processJoin(join, nestedJoin)
            }
        }
    }
}
