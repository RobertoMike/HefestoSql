package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.actions.Order
import io.github.robertomike.hefesto.constructors.ConstructOrder
import io.github.robertomike.hefesto.enums.Sort
import jakarta.persistence.metamodel.SingularAttribute
import kotlin.reflect.KProperty1

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

    // ========== TYPE-SAFE ORDER BY SUPPORT ==========

    /**
     * Type-safe orderBy using JPA Metamodel SingularAttribute.
     *
     * @param attribute the JPA metamodel attribute (e.g., User_.name)
     * @param sort the sort order
     * @return the updated builder object
     */
    fun <T, V> orderBy(attribute: SingularAttribute<T, V>, sort: Sort): R {
        return orderBy(attribute.name, sort)
    }

    /**
     * Type-safe orderBy using JPA Metamodel SingularAttribute (defaults to ASC).
     *
     * @param attribute the JPA metamodel attribute
     * @return the updated builder object
     */
    fun <T, V> orderBy(attribute: SingularAttribute<T, V>): R {
        return orderBy(attribute.name, Sort.ASC)
    }

    /**
     * Type-safe orderBy using Kotlin property reference.
     *
     * @param property the Kotlin property reference (e.g., User::name)
     * @param sort the sort order
     * @return the updated builder object
     */
    fun <T, V> orderBy(property: KProperty1<T, V>, sort: Sort): R {
        return orderBy(property.name, sort)
    }

    /**
     * Type-safe orderBy using Kotlin property reference (defaults to ASC).
     *
     * @param property the Kotlin property reference
     * @return the updated builder object
     */
    fun <T, V> orderBy(property: KProperty1<T, V>): R {
        return orderBy(property.name, Sort.ASC)
    }

    // ========== END TYPE-SAFE ORDER BY SUPPORT ==========
}
