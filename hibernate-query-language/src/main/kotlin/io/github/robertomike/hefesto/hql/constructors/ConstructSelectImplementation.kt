package io.github.robertomike.hefesto.hql.constructors

import io.github.robertomike.hefesto.actions.Select
import io.github.robertomike.hefesto.hql.builders.Hefesto
import io.github.robertomike.hefesto.constructors.ConstructSelect
import io.github.robertomike.hefesto.enums.SelectOperator
import io.github.robertomike.hefesto.exceptions.QueryException

/**
 * HQL implementation of SELECT clause construction.
 * 
 * Converts SELECT definitions into HQL string-based select clauses.
 * Supports:
 * - Simple field selections
 * - Aggregate functions (COUNT, SUM, AVG, MIN, MAX)
 * - Field aliases for result mapping
 * - Automatic table alias prefixing
 * - Constructor expressions for DTO projections
 * 
 * Example output: "select user.id, user.name as userName, count(order.id) as orderCount"
 */
class ConstructSelectImplementation : ConstructSelect() {
    private val prefix = "select "
    private lateinit var hefesto: Hefesto<*>
    private var nested = false

    /**
     * Constructs the SELECT clause as an HQL string.
     *
     * @param builder the Hefesto builder containing table alias information
     * @return the HQL SELECT clause string (e.g., "select user.name, user.email")
     */
    fun construct(builder: Hefesto<*>): String {
        this.hefesto = builder
        val select = items.stream()
            .map { getSelectField(it) }
            .reduce { a, b -> "$a, $b" }
            .orElse(null)

        if (select == null) {
            return ""
        }

        return prefix + select
    }

    private fun getSelectField(select: Select): String {
        var field = select.field

        if (!field.contains(".")) {
            field = "${hefesto.acronymTable}.$field"
        }

        if (select.operator != null) {
            return addAlias(getWithFunction(field, select.operator!!), select)
        }

        return addAlias(field, select)
    }

    private fun getWithFunction(field: String, operator: SelectOperator): String {
        return when (operator) {
            SelectOperator.SUM -> "sum($field)"
            SelectOperator.AVG -> "avg($field)"
            SelectOperator.MAX -> "max($field)"
            SelectOperator.MIN -> "min($field)"
            SelectOperator.COUNT -> "count($field)"
        }
    }

    private fun addAlias(function: String, select: Select): String {
        if (select.alias != null) {
            return "$function as ${select.alias!!.replace(".", "_")}"
        }

        return if (nested) function else "$function as ${select.field.replace(".", "_")}"
    }

    fun constructSubQuery(builder: Hefesto<*>): String {
        nested = true

        if (isEmpty()) {
            return prefix + builder.acronymTable
        }

        if (size != 1) {
            throw QueryException("Sub-query must have only one select when is using Where IN operation")
        }

        return prefix + getSelectField(items[0])
    }
}
