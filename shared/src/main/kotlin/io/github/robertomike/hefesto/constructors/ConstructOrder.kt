package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.Order

/**
 * Abstract base class for constructing ORDER BY clause components.
 * Collects and manages ordering directives that will be applied to query results.
 * 
 * Implementations of this class are responsible for converting the collected
 * ORDER BY clauses into the appropriate format for their query builder
 * (Criteria API or HQL).
 */
abstract class ConstructOrder : Construct<Order>()
