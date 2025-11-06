package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.Join

abstract class ConstructJoin : Construct<Join>() {
    /**
     * Gets the list of join definitions for testing purposes
     */
    fun getJoinDefinitions(): List<Join> = items.toList()
}
