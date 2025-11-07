package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.builders.Hefesto
import io.github.robertomike.hefesto.enums.JoinOperator
import io.github.robertomike.hefesto.enums.SelectOperator
import io.github.robertomike.hefesto.enums.WhereOperator
import io.github.robertomike.hefesto.models.BaseModel

/**
 * Context class for building subqueries with a fluent lambda-based API.
 * Provides convenient methods for configuring subquery behavior inline.
 *
 * Example usage:
 * ```java
 * var users = Hefesto.make(User.class)
 *     .whereIn("id", UserPet.class, subQuery -> {
 *         subQuery.addSelect("user.id");
 *         subQuery.where("petType", "DOG");
 *     })
 *     .get();
 * ```
 *
 * @param T The entity type for the subquery
 */
class SubQueryContext<T : BaseModel>(private val builder: Hefesto<T>) {

    /**
     * Adds a select field to the subquery.
     * @param field The field name to select
     */
    fun addSelect(field: String): SubQueryContext<T> {
        builder.addSelect(field)
        return this
    }

    /**
     * Adds a select field with an alias.
     * @param field The field name to select
     * @param alias The alias for the field
     */
    fun addSelect(field: String, alias: String): SubQueryContext<T> {
        builder.addSelect(field, alias)
        return this
    }

    /**
     * Adds a select field with a SelectOperator.
     * @param field The field name to select
     * @param operator The select operator (COUNT, SUM, AVG, etc.)
     */
    fun addSelect(field: String, operator: SelectOperator): SubQueryContext<T> {
        builder.addSelect(field, operator)
        return this
    }

    /**
     * Adds a select field with an alias and SelectOperator.
     * @param field The field name to select
     * @param alias The alias for the field
     * @param operator The select operator (COUNT, SUM, AVG, etc.)
     */
    fun addSelect(field: String, alias: String, operator: SelectOperator): SubQueryContext<T> {
        builder.addSelect(field, alias, operator)
        return this
    }

    // Aggregate shortcuts
    fun count(field: String = "*"): SubQueryContext<T> {
        builder.count(field)
        return this
    }

    fun count(field: String, alias: String): SubQueryContext<T> {
        builder.count(field, alias)
        return this
    }

    fun sum(field: String): SubQueryContext<T> {
        builder.sum(field)
        return this
    }

    fun sum(field: String, alias: String): SubQueryContext<T> {
        builder.sum(field, alias)
        return this
    }

    fun avg(field: String): SubQueryContext<T> {
        builder.avg(field)
        return this
    }

    fun avg(field: String, alias: String): SubQueryContext<T> {
        builder.avg(field, alias)
        return this
    }

    fun min(field: String): SubQueryContext<T> {
        builder.min(field)
        return this
    }

    fun min(field: String, alias: String): SubQueryContext<T> {
        builder.min(field, alias)
        return this
    }

    fun max(field: String): SubQueryContext<T> {
        builder.max(field)
        return this
    }

    fun max(field: String, alias: String): SubQueryContext<T> {
        builder.max(field, alias)
        return this
    }

    // Where methods
    fun where(field: String, value: Any?): SubQueryContext<T> {
        builder.where(field, value)
        return this
    }

    fun whereIsNull(field: String): SubQueryContext<T> {
        builder.whereIsNull(field)
        return this
    }

    fun whereIsNotNull(field: String): SubQueryContext<T> {
        builder.whereIsNotNull(field)
        return this
    }

    fun <V> whereIn(field: String, vararg values: V): SubQueryContext<T> {
        builder.whereIn(field, *values)
        return this
    }

    fun <V> whereIn(field: String, values: Iterable<V>): SubQueryContext<T> {
        builder.whereIn(field, values)
        return this
    }

    fun <V> whereNotIn(field: String, vararg values: V): SubQueryContext<T> {
        builder.whereNotIn(field, *values)
        return this
    }

    fun <V> whereNotIn(field: String, values: Iterable<V>): SubQueryContext<T> {
        builder.whereNotIn(field, values)
        return this
    }

    fun whereRaw(raw: String): SubQueryContext<T> {
        builder.whereRaw(raw)
        return this
    }

    // Join methods
    fun join(table: String): SubQueryContext<T> {
        builder.join(table)
        return this
    }

    fun join(table: String, operator: JoinOperator): SubQueryContext<T> {
        builder.join(table, operator)
        return this
    }

    // Group by
    fun groupBy(vararg fields: String): SubQueryContext<T> {
        builder.groupBy(*fields)
        return this
    }

    // Order by
    fun orderBy(field: String): SubQueryContext<T> {
        builder.orderBy(field)
        return this
    }

    // Limit/Offset
    fun limit(limit: Int): SubQueryContext<T> {
        builder.limit = limit
        return this
    }

    fun offset(offset: Int): SubQueryContext<T> {
        builder.offset = offset
        return this
    }

    /**
     * Gets the underlying Hefesto builder.
     * @return The Hefesto builder instance
     */
    fun getBuilder(): Hefesto<T> {
        return builder
    }
}
