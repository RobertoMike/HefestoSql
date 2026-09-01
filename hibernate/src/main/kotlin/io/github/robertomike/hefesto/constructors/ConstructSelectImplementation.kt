package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.Select
import io.github.robertomike.hefesto.enums.SelectOperator
import io.github.robertomike.hefesto.exceptions.QueryException
import io.github.robertomike.hefesto.models.BaseModel
import io.github.robertomike.hefesto.utils.HibernateUtils
import jakarta.persistence.criteria.*

/**
 * Criteria API implementation of SELECT clause construction.
 * 
 * Converts SELECT definitions into JPA Criteria API Selection objects.
 * Supports:
 * - Simple field selections
 * - Aggregate functions (COUNT, SUM, AVG, MIN, MAX)
 * - Field aliases for result mapping
 * - Selections from joined entities
 * - DTO projections via constructor expressions
 * 
 * When no selections are specified, defaults to selecting the entire root entity.
 * For DTO projections, creates a tuple or uses constructor expressions.
 */
class ConstructSelectImplementation<T : BaseModel> : ConstructSelect() {
    private var joins: Map<String, Join<*, *>> = emptyMap()
    private lateinit var cb: CriteriaBuilder

    /**
     * Sets the join map for resolving select fields on joined entities.
     *
     * @param joins map of join aliases to Join objects
     * @return this instance for chaining
     */
    fun setJoins(joins: Map<String, Join<*, *>>): ConstructSelectImplementation<T> {
        this.joins = joins
        return this
    }

    /**
     * Constructs the SELECT clause for a standard entity query.
     * If no selections defined, selects the entire root entity.
     *
     * @param root the root entity
     * @param cr the CriteriaQuery to apply selections to
     * @param cb the CriteriaBuilder for creating expressions
     */
    fun construct(root: Root<T>, cr: CriteriaQuery<T>, cb: CriteriaBuilder) {
        if (isEmpty()) {
            cr.select(root)
            return
        }

        multiSelect(root, cr, cb)
    }

    /**
     * Constructs a multi-select query for DTO projections or tuple results.
     * Used when selecting specific fields rather than entire entities.
     *
     * @param root the root entity
     * @param cr the CriteriaQuery to apply selections to
     * @param cb the CriteriaBuilder for creating expressions
     * @param isProjection true if projecting to a DTO class (requires multiselect)
     */
    fun multiSelect(root: Root<*>, cr: CriteriaQuery<*>, cb: CriteriaBuilder, isProjection: Boolean = false) {
        this.cb = cb

        val selects = mutableListOf<Selection<*>>()

        items.forEach { element ->
            var select: Selection<*> = getSelectField(root, element)

            if (element.alias != null) {
                select = select.alias(element.alias)
            }

            selects.add(select)
        }

        // For a single select without alias, try using select() for simple types
        // BUT not when projecting to a DTO, as multiselect() is required for constructor calls
        if (selects.size == 1 && items[0].alias == null && !isProjection) {
            try {
                @Suppress("UNCHECKED_CAST")
                (cr as CriteriaQuery<Any>).select(selects[0] as Selection<Any>)
            } catch (e: Exception) {
                // Fallback to multiselect if select() doesn't work
                cr.multiselect(*selects.toTypedArray())
            }
        } else {
            cr.multiselect(*selects.toTypedArray())
        }
    }

    private fun getSelectField(root: Root<*>, element: Select): Expression<*> {
        var from: From<*, *> = root
        var field = element.field

        if (element.field.contains(".") && joins.containsKey(field.split(HibernateUtils.DOT_REGEX.toRegex())[0])) {
            val splitted = field.split(HibernateUtils.DOT_REGEX.toRegex())
            from = joins[splitted[0]]!!
            field = splitted[1]
        }

        if (field.contains("*")) {
            return from
        }

        var select: Expression<*> = HibernateUtils.getFieldFrom<Any>(from, field)

        if (element.operator != null) {
            select = getWithFunction(select, element.operator!!)
        }

        return select
    }

    @Suppress("UNCHECKED_CAST")
    private fun getWithFunction(select: Expression<*>, operator: SelectOperator): Expression<*> {
        return when (operator) {
            SelectOperator.SUM -> cb.sum(select as Expression<Number>)
            SelectOperator.AVG -> cb.avg(select as Expression<Number>)
            SelectOperator.MAX -> cb.max(select as Expression<Number>)
            SelectOperator.MIN -> cb.min(select as Expression<Number>)
            SelectOperator.COUNT -> cb.count(select)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun constructSubQuery(root: Root<*>, sub: Subquery<*>) {
        if (isEmpty()) {
            (sub as Subquery<Any>).select(root as Expression<Any>)
            return
        }

        if (size != 1) {
            throw QueryException("Sub-query must have only one select when is using Where IN operation")
        }

        (sub as Subquery<Any>).select(getSelectField(root, items[0]) as Expression<Any>)
    }
}
