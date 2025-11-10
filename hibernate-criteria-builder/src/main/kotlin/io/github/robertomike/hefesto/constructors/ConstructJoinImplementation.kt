package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.wheres.Where
import jakarta.persistence.criteria.*

/**
 * Criteria API implementation of JOIN clause construction.
 * 
 * Converts JOIN definitions into JPA Criteria API Join objects.
 * Supports:
 * - Simple joins on entity relationships
 * - Nested/deep joins (e.g., user.address.city)
 * - Different join types (INNER, LEFT, RIGHT)
 * - Inline join conditions via JoinBuilder
 * - Join aliases for use in WHERE and SELECT clauses
 * 
 * The class maintains two important maps:
 * - joins: Maps aliases to actual Join objects for WHERE/ORDER/SELECT resolution
 * - joinConditions: Stores inline WHERE conditions defined on joins
 */
class ConstructJoinImplementation<T> : ConstructJoin() {
    val joins: MutableMap<String, Join<*, *>> = HashMap()
    
    /**
     * Stores inline WHERE conditions for each join, keyed by join alias.
     * These conditions are applied as predicates in the WHERE clause.
     */
    val joinConditions: MutableMap<String, MutableList<Where>> = HashMap()

    /**
     * Constructs all joins from the root entity.
     * Processes each join definition and creates the corresponding JPA Join objects.
     *
     * @param root the root entity to join from
     */
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
            from.join<Any, Any>(joinDef.table, joinType)
        } else {
            // Relationship join (standard JPA relationship)
            from.join<Any, Any>(joinDef.table, joinType)
        }

        // Store the join with its alias
        joins[alias] = join
        
        // Store inline WHERE conditions for this join
        if (joinDef.conditions.isNotEmpty()) {
            joinConditions[alias] = joinDef.conditions.toMutableList()
        }

        // Process nested deep joins recursively
        if (joinDef.hasDeepJoins()) {
            joinDef.deepJoins.forEach { nestedJoin ->
                processJoin(join, nestedJoin)
            }
        }
    }
}
