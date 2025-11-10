package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.Join

/**
 * Abstract base class for constructing JOIN clause components.
 * Collects and manages JOIN definitions including nested/deep joins and inline conditions.
 * 
 * Implementations of this class are responsible for converting the collected
 * JOIN configurations into the appropriate format for their query builder
 * (Criteria API or HQL).
 */
abstract class ConstructJoin : Construct<Join>() {
    /**
     * Gets the list of join definitions for testing purposes.
     * 
     * @return immutable list of all configured joins
     */
    fun getJoinDefinitions(): List<Join> = items.toList()
}
