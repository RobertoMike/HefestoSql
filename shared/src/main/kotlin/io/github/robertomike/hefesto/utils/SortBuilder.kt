package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.actions.Order
import io.github.robertomike.hefesto.constructors.ConstructOrder
import io.github.robertomike.hefesto.enums.Sort

/**
 * This class is used to build a WHERE clause.
 *
 * @param <R> is the builder
 */
@Suppress("UNCHECKED_CAST")
interface SortBuilder<R : SortBuilder<R>> {

    /**
     * Returns the instance of ConstructOrder.
     *
     * @return the instance of ConstructOrder
     */
    val orders: ConstructOrder

    /**
     * Adds an order to the list of orders for sorting.
     *
     * @param field the field to sort on
     * @param sort  the sort order
     * @return the updated builder object
     */
    fun orderBy(field: String, sort: Sort): R {
        orders.add(Order(field, sort))
        return this as R
    }

    /**
     * This allows you to order by multiple fields
     *
     * @param fields the fields to order by
     * @return same instance
     */
    fun orderBy(vararg fields: String): R {
        for (field in fields) {
            orders.add(Order(field))
        }
        return this as R
    }

    /**
     * This allows you to order by multiple fields
     *
     * @param orders the fields to order by
     * @return same instance
     */
    fun orderBy(vararg orders: Order): R {
        this.orders.addAll(orders.toList())
        return this as R
    }
}
