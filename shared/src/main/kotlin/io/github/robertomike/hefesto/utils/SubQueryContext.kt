package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.builders.BaseBuilder
import io.github.robertomike.hefesto.models.BaseModel

/**
 * Context class for building subqueries with lambda expressions.
 * This class provides a fluent API for creating subqueries inline without
 * needing to pre-build Hefesto instances.
 * 
 * Usage:
 * ```
 * // Java - automatically infers result type
 * whereIn("id", UserPet.class, subQuery -> {
 *     subQuery.addSelect("user.id");
 *     subQuery.where("active", true);
 * })
 * 
 * // Kotlin
 * whereIn("id", UserPet::class.java) {
 *     addSelect("user.id")
 *     where("active", true)
 * }
 * ```
 * 
 * @param BUILDER the type of builder being configured
 */
class SubQueryContext<BUILDER : BaseBuilder<*, *, *, *, *, *, *, *>>(
    private val subQueryBuilder: BUILDER
) {
    
    /**
     * Adds a WHERE condition to the subquery.
     *
     * @param field the field name
     * @param value the value to compare
     * @return this context for chaining
     */
    fun where(field: String, value: Any?): SubQueryContext<BUILDER> {
        subQueryBuilder.where(field, value)
        return this
    }

    /**
     * Adds a WHERE condition with operator to the subquery.
     *
     * @param field the field name
     * @param operator the comparison operator
     * @param value the value to compare
     * @return this context for chaining
     */
    fun where(field: String, operator: io.github.robertomike.hefesto.enums.Operator, value: Any?): SubQueryContext<BUILDER> {
        subQueryBuilder.where(field, operator, value)
        return this
    }

    /**
     * Adds a WHERE IS NULL condition to the subquery.
     *
     * @param field the field name
     * @return this context for chaining
     */
    fun whereIsNull(field: String): SubQueryContext<BUILDER> {
        subQueryBuilder.whereIsNull(field)
        return this
    }

    /**
     * Adds a WHERE IS NOT NULL condition to the subquery.
     *
     * @param field the field name
     * @return this context for chaining
     */
    fun whereIsNotNull(field: String): SubQueryContext<BUILDER> {
        subQueryBuilder.whereIsNotNull(field)
        return this
    }

    /**
     * Adds a WHERE IN condition to the subquery.
     *
     * @param field the field name
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T> whereIn(field: String, vararg values: T): SubQueryContext<BUILDER> {
        subQueryBuilder.whereIn(field, *values)
        return this
    }

    /**
     * Adds a WHERE IN condition with iterable values to the subquery.
     *
     * @param field the field name
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T> whereIn(field: String, values: Iterable<T>): SubQueryContext<BUILDER> {
        subQueryBuilder.whereIn(field, values)
        return this
    }

    /**
     * Adds a WHERE NOT IN condition to the subquery.
     *
     * @param field the field name
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T> whereNotIn(field: String, vararg values: T): SubQueryContext<BUILDER> {
        subQueryBuilder.whereNotIn(field, *values)
        return this
    }

    /**
     * Adds a WHERE NOT IN condition with iterable values to the subquery.
     *
     * @param field the field name
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T> whereNotIn(field: String, values: Iterable<T>): SubQueryContext<BUILDER> {
        subQueryBuilder.whereNotIn(field, values)
        return this
    }

    /**
     * Adds a SELECT field to the subquery.
     *
     * @param field the field name to select
     * @return this context for chaining
     */
    fun addSelect(field: String): SubQueryContext<BUILDER> {
        subQueryBuilder.addSelect(field)
        return this
    }

    /**
     * Adds a SELECT field with alias to the subquery.
     *
     * @param field the field name to select
     * @param alias the alias for the field
     * @return this context for chaining
     */
    fun addSelect(field: String, alias: String): SubQueryContext<BUILDER> {
        subQueryBuilder.addSelect(field, alias)
        return this
    }

    /**
     * Adds a SELECT field with operator to the subquery.
     *
     * @param field the field name to select
     * @param operator the select operator (COUNT, SUM, etc.)
     * @return this context for chaining
     */
    fun addSelect(field: String, operator: io.github.robertomike.hefesto.enums.SelectOperator): SubQueryContext<BUILDER> {
        subQueryBuilder.addSelect(field, operator)
        return this
    }

    /**
     * Adds an ORDER BY clause to the subquery.
     *
     * @param field the field to order by
     * @return this context for chaining
     */
    fun orderBy(field: String): SubQueryContext<BUILDER> {
        subQueryBuilder.orderBy(field)
        return this
    }

    /**
     * Adds an ORDER BY clause with sort direction to the subquery.
     *
     * @param field the field to order by
     * @param sort the sort direction (ASC/DESC)
     * @return this context for chaining
     */
    fun orderBy(field: String, sort: io.github.robertomike.hefesto.enums.Sort): SubQueryContext<BUILDER> {
        subQueryBuilder.orderBy(field, sort)
        return this
    }

    /**
     * Adds a GROUP BY clause to the subquery.
     *
     * @param field the field to group by
     * @return this context for chaining
     */
    fun groupBy(field: String): SubQueryContext<BUILDER> {
        subQueryBuilder.groupBy(field)
        return this
    }

    /**
     * Sets a limit on the subquery results.
     *
     * @param limit the maximum number of results
     * @return this context for chaining
     */
    fun limit(limit: Int): SubQueryContext<BUILDER> {
        subQueryBuilder.limit = limit
        return this
    }

    /**
     * Sets an offset for the subquery results.
     *
     * @param offset the number of results to skip
     * @return this context for chaining
     */
    fun offset(offset: Int): SubQueryContext<BUILDER> {
        subQueryBuilder.offset = offset
        return this
    }

    /**
     * Adds a grouped WHERE condition using whereAny.
     *
     * @param block the lambda to configure the group
     * @return this context for chaining
     */
    fun whereAny(block: java.util.function.Consumer<WhereGroupContext>): SubQueryContext<BUILDER> {
        subQueryBuilder.whereAny(block)
        return this
    }

    /**
     * Adds a grouped WHERE condition using whereAll.
     *
     * @param block the lambda to configure the group
     * @return this context for chaining
     */
    fun whereAll(block: java.util.function.Consumer<WhereGroupContext>): SubQueryContext<BUILDER> {
        subQueryBuilder.whereAll(block)
        return this
    }

    /**
     * Gets the configured builder instance.
     * Use this to access additional builder methods not exposed directly by SubQueryContext.
     * 
     * Example:
     * ```
     * // Java
     * whereExists(UserPet.class, subQuery -> {
     *     subQuery.getBuilder().whereField("user.id", "id"); // Correlate fields
     *     subQuery.where("active", true);
     * })
     * ```
     *
     * @return the configured builder
     */
    fun getBuilder(): BUILDER {
        return subQueryBuilder
    }
}
