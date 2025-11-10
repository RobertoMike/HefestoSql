package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.wheres.BaseWhere

/**
 * Abstract base class for constructing WHERE clause components.
 * Collects and manages WHERE conditions that will be transformed into predicates.
 * 
 * Implementations of this class are responsible for converting the collected
 * WHERE conditions into the appropriate format for their query builder
 * (Criteria API or HQL).
 */
abstract class ConstructWhere : Construct<BaseWhere>()
