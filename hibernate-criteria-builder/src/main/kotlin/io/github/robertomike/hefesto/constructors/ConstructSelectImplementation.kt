package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.Select
import io.github.robertomike.hefesto.enums.SelectOperator
import io.github.robertomike.hefesto.exceptions.QueryException
import io.github.robertomike.hefesto.models.BaseModel
import io.github.robertomike.hefesto.utils.HibernateUtils
import jakarta.persistence.criteria.*

class ConstructSelectImplementation<T : BaseModel> : ConstructSelect() {
    private var joins: Map<String, Join<*, *>> = emptyMap()
    private lateinit var cb: CriteriaBuilder

    fun setJoins(joins: Map<String, Join<*, *>>): ConstructSelectImplementation<T> {
        this.joins = joins
        return this
    }

    fun construct(root: Root<T>, cr: CriteriaQuery<T>, cb: CriteriaBuilder) {
        if (isEmpty()) {
            cr.select(root)
            return
        }

        multiSelect(root, cr, cb)
    }

    fun multiSelect(root: Root<*>, cr: CriteriaQuery<*>, cb: CriteriaBuilder) {
        this.cb = cb

        val selects = mutableListOf<Selection<*>>()

        items.forEach { element ->
            var select: Selection<*> = getSelectField(root, element)

            if (element.alias != null) {
                select = select.alias(element.alias)
            }

            selects.add(select)
        }

        cr.multiselect(*selects.toTypedArray())
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
