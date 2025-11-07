package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.actions.wheres.BaseWhere
import io.github.robertomike.hefesto.actions.wheres.CollectionWhere
import io.github.robertomike.hefesto.actions.wheres.Where
import io.github.robertomike.hefesto.actions.wheres.WhereExist
import io.github.robertomike.hefesto.builders.BaseBuilder
import io.github.robertomike.hefesto.constructors.ConstructWhere
import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.enums.WhereOperator
import jakarta.persistence.metamodel.SingularAttribute
import kotlin.reflect.KProperty1

/**
 * This class is used to build a WHERE clause.
 *
 * @param <R> is the builder
 */
@Suppress("UNCHECKED_CAST")
interface ConditionalBuilder<R : ConditionalBuilder<R>> {
    /**
     * Returns the 'Constructor of Where' object.
     *
     * @return the 'Constructor of Where' object
     */
    val wheres: ConstructWhere

    /**
     * Adds a WHERE condition to check if the specified field is null.
     *
     * @param field the name of the field to check
     * @return the modified object of type R
     */
    fun whereIsNull(field: String): R {
        wheres.add(Where(field, Operator.IS_NULL))
        return this as R
    }

    /**
     * Adds a "where" clause to the query to check if the given field is not null.
     *
     * @param field the name of the field to check
     * @return the updated query object
     */
    fun whereIsNotNull(field: String): R {
        wheres.add(Where(field, Operator.IS_NOT_NULL))
        return this as R
    }

    /**
     * Adds a new OR condition to the list of WHERE conditions, where the specified field is null.
     *
     * @param field the field to check for null value
     * @return the updated instance of the class
     */
    fun orWhereIsNull(field: String): R {
        wheres.add(Where(field, Operator.IS_NULL, WhereOperator.OR))
        return this as R
    }

    /**
     * Add a WHERE clause to the query that checks if the value of the specified field is not null.
     *
     * @param field the name of the field to check
     * @return the modified query object
     */
    fun orWhereIsNotNull(field: String): R {
        wheres.add(Where(field, Operator.IS_NOT_NULL, WhereOperator.OR))
        return this as R
    }

    /**
     * Adds a list of WHERE conditions, this creates a CollectionWhere.
     * This method adds parenthesis to the where's inserted here.
     *
     * @param whereList the list of BaseWhere objects to be added
     * @return the updated object of type R
     */
    fun <T : BaseWhere> where(vararg whereList: T): R {
        wheres.add(CollectionWhere(whereList.toList()))
        return this as R
    }

    /**
     * Adds a list of WHERE conditions using the OR operator, this creates a CollectionWhere.
     * This method adds parenthesis to the where's inserted here.
     *
     * @param whereList a list of BaseWhere conditions to be added
     * @return the modified object of type R
     */
    fun <T : BaseWhere> orWhere(vararg whereList: T): R {
        wheres.add(CollectionWhere(whereList.toList(), WhereOperator.OR))
        return this as R
    }

    /**
     * Adds an OR condition to the query by specifying a field and a value.
     *
     * @param field the field to compare
     * @param value the value to compare against
     * @return the modified object
     */
    fun orWhere(field: String, value: Any?): R {
        if (value != null) {
            wheres.add(Where(field, Operator.EQUAL, value, WhereOperator.OR))
        }
        return this as R
    }

    /**
     * Adds an "OR" condition to the query by specifying the field, operator, and value.
     *
     * @param field    the field to compare against
     * @param operator the comparison operator
     * @param value    the value to compare with
     * @return the updated query object
     */
    fun orWhere(field: String, operator: Operator, value: Any?): R {
        if (value != null) {
            wheres.add(Where(field, operator, value, WhereOperator.OR))
        }
        return this as R
    }

    /**
     * A description of the entire Java function.
     *
     * @param field    description of parameter
     * @param operator description of parameter
     * @param value    description of parameter
     * @return description of return value
     */
    fun where(field: String, operator: Operator, value: Any?): R {
        if (value != null) {
            wheres.add(Where(field, operator, value))
        }
        return this as R
    }

    /**
     * Adds a where clause to the query if the value is not null.
     *
     * @param field the field to compare
     * @param value the value to compare against
     * @return the modified query object
     */
    fun where(field: String, value: Any?): R {
        if (value != null) {
            wheres.add(Where(field, Operator.EQUAL, value))
        }
        return this as R
    }

    // ========== TYPE-SAFE PROPERTY REFERENCE SUPPORT ==========

    /**
     * Type-safe where clause using JPA Metamodel SingularAttribute (for Java).
     *
     * @param attribute the JPA metamodel attribute (e.g., User_.name)
     * @param operator the comparison operator
     * @param value the value to compare against
     * @return the modified query object
     */
    fun <T, V> where(attribute: SingularAttribute<T, V>, operator: Operator, value: V?): R {
        return where(attribute.name, operator, value)
    }

    /**
     * Type-safe where clause using JPA Metamodel SingularAttribute (for Java).
     *
     * @param attribute the JPA metamodel attribute (e.g., User_.name)
     * @param value the value to compare against (uses EQUAL operator)
     * @return the modified query object
     */
    fun <T, V> where(attribute: SingularAttribute<T, V>, value: V?): R {
        return where(attribute.name, value)
    }

    /**
     * Type-safe where clause using Kotlin property reference.
     *
     * @param property the Kotlin property reference (e.g., User::name)
     * @param operator the comparison operator
     * @param value the value to compare against
     * @return the modified query object
     */
    fun <T, V> where(property: KProperty1<T, V>, operator: Operator, value: V?): R {
        return where(property.name, operator, value)
    }

    /**
     * Type-safe where clause using Kotlin property reference.
     *
     * @param property the Kotlin property reference (e.g., User::name)
     * @param value the value to compare against (uses EQUAL operator)
     * @return the modified query object
     */
    fun <T, V> where(property: KProperty1<T, V>, value: V?): R {
        return where(property.name, value)
    }

    /**
     * Type-safe OR where clause using JPA Metamodel SingularAttribute.
     *
     * @param attribute the JPA metamodel attribute
     * @param operator the comparison operator
     * @param value the value to compare against
     * @return the modified query object
     */
    fun <T, V> orWhere(attribute: SingularAttribute<T, V>, operator: Operator, value: V?): R {
        return orWhere(attribute.name, operator, value)
    }

    /**
     * Type-safe OR where clause using JPA Metamodel SingularAttribute.
     *
     * @param attribute the JPA metamodel attribute
     * @param value the value to compare against
     * @return the modified query object
     */
    fun <T, V> orWhere(attribute: SingularAttribute<T, V>, value: V?): R {
        return orWhere(attribute.name, value)
    }

    /**
     * Type-safe OR where clause using Kotlin property reference.
     *
     * @param property the Kotlin property reference
     * @param operator the comparison operator
     * @param value the value to compare against
     * @return the modified query object
     */
    fun <T, V> orWhere(property: KProperty1<T, V>, operator: Operator, value: V?): R {
        return orWhere(property.name, operator, value)
    }

    /**
     * Type-safe OR where clause using Kotlin property reference.
     *
     * @param property the Kotlin property reference
     * @param value the value to compare against
     * @return the modified query object
     */
    fun <T, V> orWhere(property: KProperty1<T, V>, value: V?): R {
        return orWhere(property.name, value)
    }

    /**
     * Type-safe whereIsNull using JPA Metamodel SingularAttribute.
     *
     * @param attribute the JPA metamodel attribute
     * @return the modified query object
     */
    fun <T, V> whereIsNull(attribute: SingularAttribute<T, V>): R {
        return whereIsNull(attribute.name)
    }

    /**
     * Type-safe whereIsNull using Kotlin property reference.
     *
     * @param property the Kotlin property reference
     * @return the modified query object
     */
    fun <T, V> whereIsNull(property: KProperty1<T, V>): R {
        return whereIsNull(property.name)
    }

    /**
     * Type-safe whereIsNotNull using JPA Metamodel SingularAttribute.
     *
     * @param attribute the JPA metamodel attribute
     * @return the modified query object
     */
    fun <T, V> whereIsNotNull(attribute: SingularAttribute<T, V>): R {
        return whereIsNotNull(attribute.name)
    }

    /**
     * Type-safe whereIsNotNull using Kotlin property reference.
     *
     * @param property the Kotlin property reference
     * @return the modified query object
     */
    fun <T, V> whereIsNotNull(property: KProperty1<T, V>): R {
        return whereIsNotNull(property.name)
    }

    // ========== END TYPE-SAFE SUPPORT ==========

    /**
     * Adds a WHERE IN clause to the query.
     *
     * @param field  the field to apply the WHERE clause on
     * @param values the values to match against
     * @return the modified query object
     */
    fun <T> whereIn(field: String, vararg values: T): R {
        wheres.add(Where(field, Operator.IN, values))
        return this as R
    }

    /**
     * Adds a WHERE IN clause to the query.
     *
     * @param field  the field to apply the WHERE clause on
     * @param values the values to match against
     * @return the modified query object
     */
    fun <T> whereIn(field: String, values: Iterable<T>): R {
        if (values.toList().isEmpty()) {
            return this as R
        }

        wheres.add(Where(field, Operator.IN, values))
        return this as R
    }

    /**
     * Adds a WHERE IN clause with a subQuery as value to the query.
     *
     * @param field    the field to apply the WHERE clause on
     * @param subQuery the subQuery for the WHERE clause
     * @return the modified query object
     */
    fun <T : BaseBuilder<*, *, *, *, *, *, *, *>> whereIn(field: String, subQuery: T): R {
        wheres.add(Where(field, Operator.IN, subQuery))
        return this as R
    }

    /**
     * Adds a WHERE NOT IN clause with a subQuery as value to the query.
     *
     * @param field    the field to apply the WHERE clause on
     * @param subQuery the subQuery for the WHERE clause
     * @return the modified query object
     */
    fun <T : BaseBuilder<*, *, *, *, *, *, *, *>> whereNotIn(field: String, subQuery: T): R {
        wheres.add(Where(field, Operator.NOT_IN, subQuery))
        return this as R
    }

    /**
     * Adds a WHERE IN clause to the query.
     *
     * @param field  the field to apply the WHERE clause on
     * @param values the values to match against
     * @return the modified query object
     */
    fun <T> orWhereIn(field: String, vararg values: T): R {
        wheres.add(Where(field, Operator.IN, values, WhereOperator.OR))
        return this as R
    }

    /**
     * Adds a WHERE IN clause to the query.
     *
     * @param field  the field to apply the WHERE clause on
     * @param values the values to match against
     * @return the modified query object
     */
    fun <T> orWhereIn(field: String, values: Iterable<T>): R {
        if (values.toList().isEmpty()) {
            return this as R
        }

        wheres.add(Where(field, Operator.IN, values, WhereOperator.OR))
        return this as R
    }

    /**
     * Adds a WHERE IN clause with a subQuery as value to the query.
     *
     * @param field    the field to apply the WHERE clause on
     * @param subQuery the subQuery for the WHERE clause
     * @return the modified query object
     */
    fun <T : BaseBuilder<*, *, *, *, *, *, *, *>> orWhereIn(field: String, subQuery: T): R {
        wheres.add(Where(field, Operator.IN, subQuery, WhereOperator.OR))
        return this as R
    }

    /**
     * Adds a WHERE NOT IN clause with a subQuery as value to the query.
     *
     * @param field    the field to apply the WHERE clause on
     * @param subQuery the subQuery for the WHERE clause
     * @return the modified query object
     */
    fun <T : BaseBuilder<*, *, *, *, *, *, *, *>> orWhereNotIn(field: String, subQuery: T): R {
        wheres.add(Where(field, Operator.NOT_IN, subQuery, WhereOperator.OR))
        return this as R
    }

    /**
     * Adds a WHERE NOT IN condition to the query.
     *
     * @param field  the field to check against
     * @param values the values to check against
     * @return the modified query object
     */
    fun <T> whereNotIn(field: String, vararg values: T): R {
        wheres.add(Where(field, Operator.NOT_IN, values))
        return this as R
    }

    /**
     * Adds a WHERE NOT IN condition to the query.
     *
     * @param field  the field to check against
     * @param values the values to check against
     * @return the modified query object
     */
    fun <T> whereNotIn(field: String, values: Iterable<T>): R {
        if (values.toList().isEmpty()) {
            return this as R
        }

        wheres.add(Where(field, Operator.NOT_IN, values))
        return this as R
    }

    // ========== TYPE-SAFE WHERE IN/NOT IN SUPPORT ==========

    /**
     * Type-safe WHERE IN using JPA Metamodel SingularAttribute.
     *
     * @param attribute the JPA metamodel attribute
     * @param values the values to match against
     * @return the modified query object
     */
    fun <T, V> whereIn(attribute: SingularAttribute<T, V>, vararg values: V): R {
        return whereIn(attribute.name, *values)
    }

    /**
     * Type-safe WHERE IN using JPA Metamodel SingularAttribute with Iterable.
     *
     * @param attribute the JPA metamodel attribute
     * @param values the values to match against
     * @return the modified query object
     */
    fun <T, V> whereIn(attribute: SingularAttribute<T, V>, values: Iterable<V>): R {
        return whereIn(attribute.name, values)
    }

    /**
     * Type-safe WHERE IN using Kotlin property reference.
     *
     * @param property the Kotlin property reference
     * @param values the values to match against
     * @return the modified query object
     */
    fun <T, V> whereIn(property: KProperty1<T, V>, vararg values: V): R {
        return whereIn(property.name, *values)
    }

    /**
     * Type-safe WHERE IN using Kotlin property reference with Iterable.
     *
     * @param property the Kotlin property reference
     * @param values the values to match against
     * @return the modified query object
     */
    fun <T, V> whereIn(property: KProperty1<T, V>, values: Iterable<V>): R {
        return whereIn(property.name, values)
    }

    /**
     * Type-safe WHERE NOT IN using JPA Metamodel SingularAttribute.
     *
     * @param attribute the JPA metamodel attribute
     * @param values the values to check against
     * @return the modified query object
     */
    fun <T, V> whereNotIn(attribute: SingularAttribute<T, V>, vararg values: V): R {
        return whereNotIn(attribute.name, *values)
    }

    /**
     * Type-safe WHERE NOT IN using JPA Metamodel SingularAttribute with Iterable.
     *
     * @param attribute the JPA metamodel attribute
     * @param values the values to check against
     * @return the modified query object
     */
    fun <T, V> whereNotIn(attribute: SingularAttribute<T, V>, values: Iterable<V>): R {
        return whereNotIn(attribute.name, values)
    }

    /**
     * Type-safe WHERE NOT IN using Kotlin property reference.
     *
     * @param property the Kotlin property reference
     * @param values the values to check against
     * @return the modified query object
     */
    fun <T, V> whereNotIn(property: KProperty1<T, V>, vararg values: V): R {
        return whereNotIn(property.name, *values)
    }

    /**
     * Type-safe WHERE NOT IN using Kotlin property reference with Iterable.
     *
     * @param property the Kotlin property reference
     * @param values the values to check against
     * @return the modified query object
     */
    fun <T, V> whereNotIn(property: KProperty1<T, V>, values: Iterable<V>): R {
        return whereNotIn(property.name, values)
    }

    // ========== END TYPE-SAFE WHERE IN/NOT IN SUPPORT ==========

    /**
     * Adds a list of WHERE conditions, this creates a CollectionWhere.
     * This method adds parenthesis to the where's inserted here.
     *
     * @param whereList the list of BaseWhere objects
     * @return the result of the function
     */
    fun where(whereList: List<out BaseWhere>): R {
        wheres.add(CollectionWhere(whereList))
        return this as R
    }

    /**
     * Adds a BaseWhere object to the list of where's and returns itself.
     *
     * @param where the BaseWhere object to be added
     * @return the modified object
     */
    fun <T : BaseWhere> where(where: T): R {
        wheres.add(where)
        return this as R
    }

    /**
     * A method to add a sub-query as a condition to the current query where need to exist.
     *
     * @param subQuery the sub-query to be added as a condition
     * @return the current builder instance
     */
    fun <T : BaseBuilder<*, *, *, *, *, *, *, *>> whereExists(subQuery: T): R {
        wheres.add(WhereExist(subQuery))
        return this as R
    }

    /**
     * A method to add a sub-query as a condition to the current query where not need exist.
     *
     * @param subQuery the sub-query to check for non-existence
     * @return the updated query builder
     */
    fun <T : BaseBuilder<*, *, *, *, *, *, *, *>> whereNotExists(subQuery: T): R {
        wheres.add(WhereExist(false, subQuery))
        return this as R
    }

    /**
     * A method to add a sub-query as a condition to the current query where need to exist.
     *
     * @param subQuery the sub-query to be added as a condition
     * @return the current builder instance
     */
    fun <T : BaseBuilder<*, *, *, *, *, *, *, *>> orWhereExists(subQuery: T): R {
        wheres.add(WhereExist(subQuery, WhereOperator.OR))
        return this as R
    }

    /**
     * A method to add a sub-query as a condition to the current query where not need exist.
     *
     * @param subQuery the sub-query to check for non-existence
     * @return the updated query builder
     */
    fun <T : BaseBuilder<*, *, *, *, *, *, *, *>> orWhereNotExists(subQuery: T): R {
        wheres.add(WhereExist(false, subQuery, WhereOperator.OR))
        return this as R
    }

    // ========== LAMBDA-BASED CONDITIONAL GROUPS ==========

    /**
     * Creates a WHERE group with OR logic between conditions.
     * All conditions added inside the block will be combined with OR operator.
     * 
     * Example:
     * ```
     * // Java
     * whereAny(group -> {
     *     group.where("status", "ACTIVE");
     *     group.where("status", "PENDING");
     * })
     * // Produces: (status='ACTIVE' OR status='PENDING')
     * 
     * // Kotlin
     * whereAny {
     *     where("status", "ACTIVE")
     *     where("status", "PENDING")
     * }
     * ```
     *
     * @param block the lambda to configure the OR group
     * @return the updated query builder
     */
    fun whereAny(block: java.util.function.Consumer<WhereGroupContext>): R {
        val context = WhereGroupContext(WhereOperator.OR)
        block.accept(context)
        if (context.conditions.isNotEmpty()) {
            wheres.add(CollectionWhere(context.conditions, WhereOperator.AND))
        }
        return this as R
    }

    /**
     * Creates a WHERE group with AND logic between conditions.
     * All conditions added inside the block will be combined with AND operator.
     * 
     * Example:
     * ```
     * // Java
     * whereAll(group -> {
     *     group.where("age", Operator.GREATER, 18);
     *     group.where("verified", true);
     * })
     * // Produces: (age>18 AND verified=true)
     * 
     * // Kotlin
     * whereAll {
     *     where("age", Operator.GREATER, 18)
     *     where("verified", true)
     * }
     * ```
     *
     * @param block the lambda to configure the AND group
     * @return the updated query builder
     */
    fun whereAll(block: java.util.function.Consumer<WhereGroupContext>): R {
        val context = WhereGroupContext(WhereOperator.AND)
        block.accept(context)
        if (context.conditions.isNotEmpty()) {
            wheres.add(CollectionWhere(context.conditions, WhereOperator.AND))
        }
        return this as R
    }

    /**
     * Creates a WHERE group with OR logic, connected to previous conditions with OR.
     * All conditions added inside the block will be combined with OR operator.
     * 
     * Example:
     * ```
     * where("country", "US")
     *     .orWhereAny {
     *         where("status", "ACTIVE")
     *         where("status", "PENDING")
     *     }
     * // Produces: country='US' OR (status='ACTIVE' OR status='PENDING')
     * ```
     *
     * @param block the lambda to configure the OR group
     * @return the updated query builder
     */
    fun orWhereAny(block: java.util.function.Consumer<WhereGroupContext>): R {
        val context = WhereGroupContext(WhereOperator.OR)
        block.accept(context)
        if (context.conditions.isNotEmpty()) {
            wheres.add(CollectionWhere(context.conditions, WhereOperator.OR))
        }
        return this as R
    }

    /**
     * Creates a WHERE group with AND logic, connected to previous conditions with OR.
     * All conditions added inside the block will be combined with AND operator.
     * 
     * Example:
     * ```
     * where("country", "US")
     *     .orWhereAll {
     *         where("age", Operator.GREATER, 18)
     *         where("verified", true)
     *     }
     * // Produces: country='US' OR (age>18 AND verified=true)
     * ```
     *
     * @param block the lambda to configure the AND group
     * @return the updated query builder
     */
    fun orWhereAll(block: java.util.function.Consumer<WhereGroupContext>): R {
        val context = WhereGroupContext(WhereOperator.AND)
        block.accept(context)
        if (context.conditions.isNotEmpty()) {
            wheres.add(CollectionWhere(context.conditions, WhereOperator.OR))
        }
        return this as R
    }
}
