package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.Select

/**
 * Abstract base class for constructing SELECT clause components.
 * Collects and manages field selections including aggregates and custom projections.
 * 
 * Implementations of this class are responsible for converting the collected
 * SELECT expressions into the appropriate format for their query builder
 * (Criteria API or HQL).
 */
abstract class ConstructSelect : Construct<Select>()
