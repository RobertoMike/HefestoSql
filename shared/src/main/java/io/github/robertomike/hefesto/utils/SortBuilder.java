package io.github.robertomike.hefesto.utils;

import io.github.robertomike.hefesto.actions.Order;
import io.github.robertomike.hefesto.actions.wheres.BaseWhere;
import io.github.robertomike.hefesto.actions.wheres.CollectionWhere;
import io.github.robertomike.hefesto.actions.wheres.Where;
import io.github.robertomike.hefesto.actions.wheres.WhereExist;
import io.github.robertomike.hefesto.builders.BaseBuilder;
import io.github.robertomike.hefesto.constructors.ConstructOrder;
import io.github.robertomike.hefesto.constructors.ConstructWhere;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.enums.Sort;
import io.github.robertomike.hefesto.enums.WhereOperator;

import java.util.Arrays;
import java.util.List;

/**
 * This class is used to build a WHERE clause.
 *
 * @param <R> is the builder
 */
@SuppressWarnings("unchecked")
public interface SortBuilder<R extends SortBuilder<R>> {

    /**
     * Returns the instance of ConstructOrder.
     *
     * @return the instance of ConstructOrder
     */
    ConstructOrder getOrders();

    /**
     * Adds an order to the list of orders for sorting.
     *
     * @param field the field to sort on
     * @param sort  the sort order
     * @return the updated builder object
     */
    default R orderBy(String field, Sort sort) {
        getOrders().add(new Order(field, sort));
        return (R) this;
    }

    /**
     * This allows you to order by multiple fields
     *
     * @param fields the fields to order by
     * @return same instance
     */
    default R orderBy(String... fields) {
        for (String field : fields) {
            getOrders().add(new Order(field));
        }
        return (R) this;
    }

    /**
     * This allows you to order by multiple fields
     *
     * @param orders the fields to order by
     * @return same instance
     */
    default R orderBy(Order... orders) {
        getOrders().addAll(orders);
        return (R) this;
    }
}
