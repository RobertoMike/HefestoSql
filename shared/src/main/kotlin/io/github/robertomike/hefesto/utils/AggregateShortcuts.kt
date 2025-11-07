package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.builders.BaseBuilder
import io.github.robertomike.hefesto.enums.SelectOperator

/**
 * Interface providing shortcut methods for common aggregate functions.
 * These methods simplify adding aggregate selections like COUNT, SUM, AVG, MIN, MAX.
 *
 * @param <B> the builder type
 */
@Suppress("UNCHECKED_CAST")
interface AggregateShortcuts<B : BaseBuilder<*, *, *, *, *, *, *, *>> {

    /**
     * Adds a COUNT aggregate function to the select clause counting all rows (COUNT(*)).
     * 
     * Example:
     * ```
     * // Java
     * Long count = Hefesto.make(User.class)
     *     .count()
     *     .findFirstFor(Long.class);
     * ```
     *
     * @return the updated builder object
     */
    fun count(): B {
        return (this as B).addSelect("*", SelectOperator.COUNT) as B
    }

    /**
     * Adds a COUNT aggregate function to the select clause.
     * 
     * Example:
     * ```
     * // Java
     * Long activeCount = Hefesto.make(User.class)
     *     .where("active", true)
     *     .count("id")
     *     .findFirstFor(Long.class);
     * ```
     *
     * @param field the field to count
     * @return the updated builder object
     */
    fun count(field: String): B {
        return (this as B).addSelect(field, SelectOperator.COUNT) as B
    }

    /**
     * Adds a COUNT aggregate function with an alias.
     * 
     * Example:
     * ```
     * // Java
     * Hefesto.make(User.class)
     *     .count("id", "totalUsers")
     *     .findFirstFor(Long.class);
     * ```
     *
     * @param field the field to count
     * @param alias the alias for the result
     * @return the updated builder object
     */
    fun count(field: String, alias: String): B {
        return (this as B).addSelect(field, alias, SelectOperator.COUNT) as B
    }

    /**
     * Adds a SUM aggregate function to the select clause.
     * 
     * Example:
     * ```
     * // Java
     * Double total = Hefesto.make(Order.class)
     *     .sum("amount")
     *     .findFirstFor(Double.class);
     * ```
     *
     * @param field the field to sum
     * @return the updated builder object
     */
    fun sum(field: String): B {
        return (this as B).addSelect(field, SelectOperator.SUM) as B
    }

    /**
     * Adds a SUM aggregate function with an alias.
     * 
     * Example:
     * ```
     * // Java
     * Hefesto.make(Order.class)
     *     .sum("amount", "totalAmount")
     *     .findFirstFor(Double.class);
     * ```
     *
     * @param field the field to sum
     * @param alias the alias for the result
     * @return the updated builder object
     */
    fun sum(field: String, alias: String): B {
        return (this as B).addSelect(field, alias, SelectOperator.SUM) as B
    }

    /**
     * Adds an AVG (average) aggregate function to the select clause.
     * 
     * Example:
     * ```
     * // Java
     * Double avgAge = Hefesto.make(User.class)
     *     .avg("age")
     *     .findFirstFor(Double.class);
     * ```
     *
     * @param field the field to average
     * @return the updated builder object
     */
    fun avg(field: String): B {
        return (this as B).addSelect(field, SelectOperator.AVG) as B
    }

    /**
     * Adds an AVG aggregate function with an alias.
     * 
     * Example:
     * ```
     * // Java
     * Hefesto.make(User.class)
     *     .avg("age", "averageAge")
     *     .findFirstFor(Double.class);
     * ```
     *
     * @param field the field to average
     * @param alias the alias for the result
     * @return the updated builder object
     */
    fun avg(field: String, alias: String): B {
        return (this as B).addSelect(field, alias, SelectOperator.AVG) as B
    }

    /**
     * Adds a MIN aggregate function to the select clause.
     * 
     * Example:
     * ```
     * // Java
     * Long minId = Hefesto.make(User.class)
     *     .min("id")
     *     .findFirstFor(Long.class);
     * ```
     *
     * @param field the field to find minimum value
     * @return the updated builder object
     */
    fun min(field: String): B {
        return (this as B).addSelect(field, SelectOperator.MIN) as B
    }

    /**
     * Adds a MIN aggregate function with an alias.
     * 
     * Example:
     * ```
     * // Java
     * Hefesto.make(User.class)
     *     .min("age", "youngestAge")
     *     .findFirstFor(Integer.class);
     * ```
     *
     * @param field the field to find minimum value
     * @param alias the alias for the result
     * @return the updated builder object
     */
    fun min(field: String, alias: String): B {
        return (this as B).addSelect(field, alias, SelectOperator.MIN) as B
    }

    /**
     * Adds a MAX aggregate function to the select clause.
     * 
     * Example:
     * ```
     * // Java
     * Long maxId = Hefesto.make(User.class)
     *     .max("id")
     *     .findFirstFor(Long.class);
     * ```
     *
     * @param field the field to find maximum value
     * @return the updated builder object
     */
    fun max(field: String): B {
        return (this as B).addSelect(field, SelectOperator.MAX) as B
    }

    /**
     * Adds a MAX aggregate function with an alias.
     * 
     * Example:
     * ```
     * // Java
     * Hefesto.make(User.class)
     *     .max("age", "oldestAge")
     *     .findFirstFor(Integer.class);
     * ```
     *
     * @param field the field to find maximum value
     * @param alias the alias for the result
     * @return the updated builder object
     */
    fun max(field: String, alias: String): B {
        return (this as B).addSelect(field, alias, SelectOperator.MAX) as B
    }
}
