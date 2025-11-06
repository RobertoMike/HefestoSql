package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.utils.HibernateUtils
import jakarta.persistence.criteria.*
import org.hibernate.QueryException

class ConstructOrderImplementation : ConstructOrder() {
    private lateinit var cb: CriteriaBuilder
    private lateinit var root: Root<*>
    private var joins: Map<String, Join<*, *>> = emptyMap()

    fun setJoins(joins: Map<String, Join<*, *>>): ConstructOrderImplementation {
        this.joins = joins
        return this
    }

    fun construct(cb: CriteriaBuilder, cr: CriteriaQuery<*>, root: Root<*>) {
        this.cb = cb
        this.root = root

        cr.orderBy(items.map { constructOrder(it) })
    }

    private fun constructOrder(value: io.github.robertomike.hefesto.actions.Order): Order {
        var from: From<*, *> = root
        var field = value.field

        if (field.contains(".") && joins.containsKey(field.split(HibernateUtils.DOT_REGEX.toRegex())[0])) {
            val splitted = field.split(HibernateUtils.DOT_REGEX.toRegex())
            from = joins[splitted[0]]!!
            field = splitted[1]
        }

        return when (value.sort) {
            io.github.robertomike.hefesto.enums.Sort.ASC -> cb.asc(from.get<Any>(field))
            io.github.robertomike.hefesto.enums.Sort.DESC -> cb.desc(from.get<Any>(field))
            else -> throw QueryException("Unsupported sort: ${value.sort}")
        }
    }
}
