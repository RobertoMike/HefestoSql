package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.actions.wheres.BaseWhere
import io.github.robertomike.hefesto.actions.wheres.CollectionWhere
import io.github.robertomike.hefesto.actions.wheres.Where
import io.github.robertomike.hefesto.builders.BaseBuilder
import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.enums.WhereOperator
import jakarta.persistence.metamodel.SingularAttribute
import kotlin.reflect.KProperty1

/**
 * Context class for building grouped WHERE conditions with AND/OR logic.
 * This class provides a fluent API for creating complex conditional groups.
 * 
 * Usage:
 * ```
 * // Java
 * whereAny(group -> {
 *     group.where("name", "test");
 *     group.where("email", "test@mail.com");
 * })
 * 
 * // Kotlin
 * whereAny {
 *     where("name", "test")
 *     where("email", "test@mail.com")
 * }
 * ```
 */
class WhereGroupContext internal constructor(
    private val groupOperator: WhereOperator = WhereOperator.OR
) {
    internal val conditions = mutableListOf<BaseWhere>()

    /**
     * Adds a WHERE condition to the group.
     *
     * @param field the field name
     * @param operator the comparison operator
     * @param value the value to compare
     * @return this context for chaining
     */
    fun where(field: String, operator: Operator, value: Any?): WhereGroupContext {
        if (value != null) {
            conditions.add(Where(field, operator, value, groupOperator))
        }
        return this
    }

    /**
     * Adds a WHERE condition with EQUALS operator.
     *
     * @param field the field name
     * @param value the value to compare
     * @return this context for chaining
     */
    fun where(field: String, value: Any?): WhereGroupContext {
        if (value != null) {
            conditions.add(Where(field, Operator.EQUAL, value, groupOperator))
        }
        return this
    }

    /**
     * Adds a WHERE IS NULL condition.
     *
     * @param field the field name
     * @return this context for chaining
     */
    fun whereIsNull(field: String): WhereGroupContext {
        conditions.add(Where(field, Operator.IS_NULL, groupOperator))
        return this
    }

    /**
     * Adds a WHERE IS NOT NULL condition.
     *
     * @param field the field name
     * @return this context for chaining
     */
    fun whereIsNotNull(field: String): WhereGroupContext {
        conditions.add(Where(field, Operator.IS_NOT_NULL, groupOperator))
        return this
    }

    /**
     * Adds a WHERE IN condition.
     *
     * @param field the field name
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T> whereIn(field: String, vararg values: T): WhereGroupContext {
        conditions.add(Where(field, Operator.IN, values, groupOperator))
        return this
    }

    /**
     * Adds a WHERE IN condition with iterable values.
     *
     * @param field the field name
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T> whereIn(field: String, values: Iterable<T>): WhereGroupContext {
        if (values.toList().isNotEmpty()) {
            conditions.add(Where(field, Operator.IN, values, groupOperator))
        }
        return this
    }

    /**
     * Adds a WHERE IN condition with subquery.
     *
     * @param field the field name
     * @param subQuery the subquery builder
     * @return this context for chaining
     */
    fun <T : BaseBuilder<*, *, *, *, *, *, *, *>> whereIn(field: String, subQuery: T): WhereGroupContext {
        conditions.add(Where(field, Operator.IN, subQuery, groupOperator))
        return this
    }

    /**
     * Adds a WHERE NOT IN condition.
     *
     * @param field the field name
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T> whereNotIn(field: String, vararg values: T): WhereGroupContext {
        conditions.add(Where(field, Operator.NOT_IN, values, groupOperator))
        return this
    }

    /**
     * Adds a WHERE NOT IN condition with iterable values.
     *
     * @param field the field name
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T> whereNotIn(field: String, values: Iterable<T>): WhereGroupContext {
        if (values.toList().isNotEmpty()) {
            conditions.add(Where(field, Operator.NOT_IN, values, groupOperator))
        }
        return this
    }

    /**
     * Adds a WHERE NOT IN condition with subquery.
     *
     * @param field the field name
     * @param subQuery the subquery builder
     * @return this context for chaining
     */
    fun <T : BaseBuilder<*, *, *, *, *, *, *, *>> whereNotIn(field: String, subQuery: T): WhereGroupContext {
        conditions.add(Where(field, Operator.NOT_IN, subQuery, groupOperator))
        return this
    }

    // ========== TYPE-SAFE PROPERTY REFERENCE SUPPORT ==========

    /**
     * Type-safe WHERE with JPA Metamodel SingularAttribute.
     *
     * @param attribute the JPA metamodel attribute
     * @param operator the comparison operator
     * @param value the value to compare
     * @return this context for chaining
     */
    fun <T, V> where(attribute: SingularAttribute<T, V>, operator: Operator, value: V?): WhereGroupContext {
        return where(attribute.name, operator, value)
    }

    /**
     * Type-safe WHERE with JPA Metamodel SingularAttribute (EQUALS).
     *
     * @param attribute the JPA metamodel attribute
     * @param value the value to compare
     * @return this context for chaining
     */
    fun <T, V> where(attribute: SingularAttribute<T, V>, value: V?): WhereGroupContext {
        return where(attribute.name, value)
    }

    /**
     * Type-safe WHERE with Kotlin property reference.
     *
     * @param property the Kotlin property reference
     * @param operator the comparison operator
     * @param value the value to compare
     * @return this context for chaining
     */
    fun <T, V> where(property: KProperty1<T, V>, operator: Operator, value: V?): WhereGroupContext {
        return where(property.name, operator, value)
    }

    /**
     * Type-safe WHERE with Kotlin property reference (EQUALS).
     *
     * @param property the Kotlin property reference
     * @param value the value to compare
     * @return this context for chaining
     */
    fun <T, V> where(property: KProperty1<T, V>, value: V?): WhereGroupContext {
        return where(property.name, value)
    }

    /**
     * Type-safe WHERE IS NULL with JPA Metamodel.
     *
     * @param attribute the JPA metamodel attribute
     * @return this context for chaining
     */
    fun <T, V> whereIsNull(attribute: SingularAttribute<T, V>): WhereGroupContext {
        return whereIsNull(attribute.name)
    }

    /**
     * Type-safe WHERE IS NULL with Kotlin property reference.
     *
     * @param property the Kotlin property reference
     * @return this context for chaining
     */
    fun <T, V> whereIsNull(property: KProperty1<T, V>): WhereGroupContext {
        return whereIsNull(property.name)
    }

    /**
     * Type-safe WHERE IS NOT NULL with JPA Metamodel.
     *
     * @param attribute the JPA metamodel attribute
     * @return this context for chaining
     */
    fun <T, V> whereIsNotNull(attribute: SingularAttribute<T, V>): WhereGroupContext {
        return whereIsNotNull(attribute.name)
    }

    /**
     * Type-safe WHERE IS NOT NULL with Kotlin property reference.
     *
     * @param property the Kotlin property reference
     * @return this context for chaining
     */
    fun <T, V> whereIsNotNull(property: KProperty1<T, V>): WhereGroupContext {
        return whereIsNotNull(property.name)
    }

    /**
     * Type-safe WHERE IN with JPA Metamodel.
     *
     * @param attribute the JPA metamodel attribute
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T, V> whereIn(attribute: SingularAttribute<T, V>, vararg values: V): WhereGroupContext {
        return whereIn(attribute.name, *values)
    }

    /**
     * Type-safe WHERE IN with JPA Metamodel and iterable.
     *
     * @param attribute the JPA metamodel attribute
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T, V> whereIn(attribute: SingularAttribute<T, V>, values: Iterable<V>): WhereGroupContext {
        return whereIn(attribute.name, values)
    }

    /**
     * Type-safe WHERE IN with Kotlin property reference.
     *
     * @param property the Kotlin property reference
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T, V> whereIn(property: KProperty1<T, V>, vararg values: V): WhereGroupContext {
        return whereIn(property.name, *values)
    }

    /**
     * Type-safe WHERE IN with Kotlin property reference and iterable.
     *
     * @param property the Kotlin property reference
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T, V> whereIn(property: KProperty1<T, V>, values: Iterable<V>): WhereGroupContext {
        return whereIn(property.name, values)
    }

    /**
     * Type-safe WHERE NOT IN with JPA Metamodel.
     *
     * @param attribute the JPA metamodel attribute
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T, V> whereNotIn(attribute: SingularAttribute<T, V>, vararg values: V): WhereGroupContext {
        return whereNotIn(attribute.name, *values)
    }

    /**
     * Type-safe WHERE NOT IN with JPA Metamodel and iterable.
     *
     * @param attribute the JPA metamodel attribute
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T, V> whereNotIn(attribute: SingularAttribute<T, V>, values: Iterable<V>): WhereGroupContext {
        return whereNotIn(attribute.name, values)
    }

    /**
     * Type-safe WHERE NOT IN with Kotlin property reference.
     *
     * @param property the Kotlin property reference
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T, V> whereNotIn(property: KProperty1<T, V>, vararg values: V): WhereGroupContext {
        return whereNotIn(property.name, *values)
    }

    /**
     * Type-safe WHERE NOT IN with Kotlin property reference and iterable.
     *
     * @param property the Kotlin property reference
     * @param values the values to check
     * @return this context for chaining
     */
    fun <T, V> whereNotIn(property: KProperty1<T, V>, values: Iterable<V>): WhereGroupContext {
        return whereNotIn(property.name, values)
    }

    // ========== NESTED GROUPS ==========

    /**
     * Creates a nested group with OR logic inside this group.
     * 
     * Example:
     * ```
     * whereAny {
     *     where("status", "ACTIVE")
     *     whereAll {  // Nested AND group
     *         where("age", Operator.GREATER, 18)
     *         where("verified", true)
     *     }
     * }
     * // Produces: (status='ACTIVE' OR (age>18 AND verified=true))
     * ```
     *
     * @param block the lambda to configure the nested group
     * @return this context for chaining
     */
    fun whereAny(block: java.util.function.Consumer<WhereGroupContext>): WhereGroupContext {
        val nestedContext = WhereGroupContext(WhereOperator.OR)
        block.accept(nestedContext)
        if (nestedContext.conditions.isNotEmpty()) {
            conditions.add(CollectionWhere(nestedContext.conditions, groupOperator))
        }
        return this
    }

    /**
     * Creates a nested group with AND logic inside this group.
     *
     * @param block the lambda to configure the nested group
     * @return this context for chaining
     */
    fun whereAll(block: java.util.function.Consumer<WhereGroupContext>): WhereGroupContext {
        val nestedContext = WhereGroupContext(WhereOperator.AND)
        block.accept(nestedContext)
        if (nestedContext.conditions.isNotEmpty()) {
            conditions.add(CollectionWhere(nestedContext.conditions, groupOperator))
        }
        return this
    }
}
