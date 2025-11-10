package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.GroupBy

/**
 * Abstract base class for constructing GROUP BY clause components.
 * Collects and manages field groupings for aggregate queries.
 * 
 * GROUP BY is typically used with aggregate functions (COUNT, SUM, AVG, etc.)
 * to group result rows by one or more columns.
 * 
 * Example usage:
 * ```java
 * Hefesto.make(User.class)
 *     .addSelect("status")
 *     .count("id", "total")
 *     .groupBy("status")
 *     .findFor(Object[].class);
 * ```
 * 
 * Implementations of this class are responsible for converting the collected
 * GROUP BY clauses into the appropriate format for their query builder
 * (Criteria API or HQL).
 */
abstract class ConstructGroupBy : Construct<GroupBy>()
