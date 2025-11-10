package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.wheres.*
import io.github.robertomike.hefesto.builders.Hefesto
import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.enums.WhereOperator
import io.github.robertomike.hefesto.exceptions.QueryException
import io.github.robertomike.hefesto.exceptions.UnsupportedOperationException
import io.github.robertomike.hefesto.utils.CastUtils
import io.github.robertomike.hefesto.utils.HibernateUtils.DOT_REGEX
import io.github.robertomike.hefesto.utils.HibernateUtils.getFieldFrom
import jakarta.persistence.criteria.*

/**
 * Criteria API implementation of WHERE clause construction.
 * 
 * Converts WHERE conditions into JPA Criteria API Predicate objects.
 * Handles various condition types including:
 * - Simple field comparisons (EQUAL, GREATER, LESS, etc.)
 * - Pattern matching (LIKE, NOT_LIKE)
 * - Collection operations (IN, NOT_IN, FIND_IN_SET)
 * - NULL checks (IS_NULL, IS_NOT_NULL)
 * - Subquery conditions (EXISTS, NOT_EXISTS)
 * - Custom predicates via lambda expressions
 * - Inline join conditions
 * 
 * This class manages join references and can apply conditions on both
 * the root entity and joined entities.
 */
class ConstructWhereImplementation : ConstructWhere() {
    protected lateinit var cb: CriteriaBuilder
    protected var cr: CriteriaQuery<*>? = null
    protected lateinit var root: Root<*>
    private var joins: Map<String, Join<*, *>> = HashMap()
    private var parentRoot: Root<*>? = null
    private var joinConditions: Map<String, List<Where>> = HashMap()

    /**
     * Sets the join map for resolving field paths on joined entities.
     *
     * @param joins map of join aliases to Join objects
     * @return this instance for chaining
     */
    fun setJoins(joins: Map<String, Join<*, *>>): ConstructWhereImplementation {
        this.joins = joins
        return this
    }
    
    /**
     * Sets the inline join conditions that were defined via JoinBuilder.
     * These conditions are applied directly on the join and combined with WHERE clause.
     *
     * @param joinConditions map of join aliases to their WHERE conditions
     * @return this instance for chaining
     */
    fun setJoinConditions(joinConditions: Map<String, List<Where>>): ConstructWhereImplementation {
        this.joinConditions = joinConditions
        return this
    }

    /**
     * Constructs and applies all WHERE predicates to the CriteriaQuery.
     * Combines regular WHERE conditions with inline join conditions.
     *
     * @param cb the CriteriaBuilder for creating predicates
     * @param cr the CriteriaQuery to apply predicates to
     * @param root the root entity
     */
    fun construct(cb: CriteriaBuilder, cr: CriteriaQuery<*>, root: Root<*>) {
        this.cr = cr
        this.cb = cb
        this.root = root

        // Collect all predicates: regular WHERE conditions + inline join conditions
        val predicates = mutableListOf<Predicate>()
        
        // Add regular WHERE conditions
        if (isNotEmpty()) {
            predicates.add(transform(items))
        }
        
        // Add inline join conditions
        joinConditions.forEach { (alias, conditions) ->
            conditions.forEach { condition ->
                val joinPredicate = createJoinPredicate(alias, condition)
                if (joinPredicate != null) {
                    predicates.add(joinPredicate)
                }
            }
        }
        
        // Apply all predicates
        if (predicates.isNotEmpty()) {
            cr.where(*predicates.toTypedArray())
        }
    }
    
    /**
     * Creates a predicate for an inline join condition
     */
    private fun createJoinPredicate(alias: String, condition: Where): Predicate? {
        val join = joins[alias] ?: return null
        
        // Use the join as the From element for this condition
        val from = join as From<*, *>
        val field = condition.field
        
        return when (condition.operator) {
            Operator.LIKE -> cb.like(
                getFieldFrom<String>(from, field),
                condition.value.toString()
            )

            Operator.NOT_LIKE -> cb.notLike(
                getFieldFrom<String>(from, field),
                condition.value.toString()
            )

            Operator.EQUAL -> {
                if (condition.value == null) {
                    cb.isNull(getFieldFrom<Any>(from, field))
                } else {
                    cb.equal(getFieldFrom<Any>(from, field), condition.value)
                }
            }

            Operator.DIFF -> {
                if (condition.value == null) {
                    cb.isNotNull(getFieldFrom<Any>(from, field))
                } else {
                    cb.notEqual(getFieldFrom<Any>(from, field), condition.value)
                }
            }

            Operator.GREATER -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                @Suppress("UNCHECKED_CAST")
                cb.greaterThan(path as Path<Comparable<Any>>, condition.value as Comparable<Any>)
            }

            Operator.LESS -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                @Suppress("UNCHECKED_CAST")
                cb.lessThan(path as Path<Comparable<Any>>, condition.value as Comparable<Any>)
            }

            Operator.GREATER_OR_EQUAL -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                @Suppress("UNCHECKED_CAST")
                cb.greaterThanOrEqualTo(path as Path<Comparable<Any>>, condition.value as Comparable<Any>)
            }

            Operator.LESS_OR_EQUAL -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                @Suppress("UNCHECKED_CAST")
                cb.lessThanOrEqualTo(path as Path<Comparable<Any>>, condition.value as Comparable<Any>)
            }

            Operator.IN -> {
                getFieldFrom<Any>(from, field).`in`(condition.value)
            }

            else -> throw UnsupportedOperationException("Unsupported operator for join condition: ${condition.operator}")
        }
    }

    private fun transform(wheres: List<BaseWhere>): Predicate {
        var lastPredicate: Predicate? = null

        wheres.forEach { value ->
            val predicate = getPredicateFromWhere(value)

            if (lastPredicate == null) {
                lastPredicate = predicate
                return@forEach
            }

            lastPredicate = applyWhereOperation(value, lastPredicate!!, predicate) // Do not remove the '!!' is useful for a smart cast in kotlin
        }

        return lastPredicate!!
    }

    fun getPredicateFromWhere(where: BaseWhere): Predicate {
        return when (where) {
            is CollectionWhere -> transform(where.wheres)
            is WhereCustom -> where.custom.call(cb, cr, root, joins, parentRoot)
            is WhereExist -> applyWhereExist(where)
            is WhereField -> constructWhereField(where)
            is Where -> constructWhere(where)
            else -> throw QueryException("Invalid class extended from BaseWhere: ${where.javaClass}")
        }
    }

    /**
     * Constructs a Predicate that compares two fields.
     * Handles field resolution from root, joins, and parent root (for subqueries).
     *
     * @param where the WhereField condition containing the two fields to compare
     * @return the constructed Predicate
     */
    private fun constructWhereField(where: WhereField): Predicate {
        var from: From<*, *> = root
        var secondFrom: From<*, *> = parentRoot ?: root
        var secondField = where.secondField
        var field = where.field

        // Resolve first field - check if it references a join
        if (field.contains(".") && joins.containsKey(field.split(DOT_REGEX.toRegex())[0])) {
            val split = field.split(DOT_REGEX.toRegex())
            from = joins[split[0]]!!
            field = split[1]
        }

        // Resolve second field - check if it references a join
        if (secondField.contains(".") && joins.containsKey(secondField.split(DOT_REGEX.toRegex())[0])) {
            val split = secondField.split(DOT_REGEX.toRegex())
            secondFrom = joins[split[0]]!!
            secondField = split[1]
        }

        return when (where.operator) {
            Operator.LIKE -> cb.like(
                getFieldFrom<String>(from, field),
                getFieldFrom<String>(secondFrom, secondField)
            )

            Operator.NOT_LIKE -> cb.notLike(
                getFieldFrom<String>(from, field),
                getFieldFrom<String>(secondFrom, secondField)
            )

            Operator.EQUAL -> cb.equal(
                getFieldFrom<Any>(from, field),
                getFieldFrom<Any>(secondFrom, secondField)
            )

            Operator.DIFF -> cb.notEqual(
                getFieldFrom<Any>(from, field),
                getFieldFrom<Any>(secondFrom, secondField)
            )

            Operator.GREATER -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                val secondPath: Path<out Comparable<*>> = getFieldFrom(secondFrom, secondField)
                @Suppress("UNCHECKED_CAST")
                cb.greaterThan(path as Path<Comparable<Any>>, secondPath as Expression<Comparable<Any>>)
            }

            Operator.LESS -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                val secondPath: Path<out Comparable<*>> = getFieldFrom(secondFrom, secondField)
                @Suppress("UNCHECKED_CAST")
                cb.lessThan(path as Path<Comparable<Any>>, secondPath as Expression<Comparable<Any>>)
            }

            Operator.GREATER_OR_EQUAL -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                val secondPath: Path<out Comparable<*>> = getFieldFrom(secondFrom, secondField)
                @Suppress("UNCHECKED_CAST")
                cb.greaterThanOrEqualTo(path as Path<Comparable<Any>>, secondPath as Expression<Comparable<Any>>)
            }

            Operator.LESS_OR_EQUAL -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                val secondPath: Path<out Comparable<*>> = getFieldFrom(secondFrom, secondField)
                @Suppress("UNCHECKED_CAST")
                cb.lessThanOrEqualTo(path as Path<Comparable<Any>>, secondPath as Expression<Comparable<Any>>)
            }

            else -> throw UnsupportedOperationException("Unsupported operator: ${where.operator}")
        }
    }

    private fun applyWhereExist(whereExist: WhereExist): Predicate {
        val subBuilder = whereExist.subQuery as Hefesto<*>
        val subQuery = subBuilder.getSubQuery(cr!!, root, cb, joins)

        return if (whereExist.exists) {
            cb.exists(subQuery)
        } else {
            cb.not(cb.exists(subQuery))
        }
    }

    private fun constructWhere(where: Where): Predicate {
        var from: From<*, *> = root
        var field = where.field

        if (field.contains(".") && joins.containsKey(field.split(DOT_REGEX.toRegex())[0])) {
            val split = field.split(DOT_REGEX.toRegex())
            from = joins[split[0]]!!
            field = split[1]
        }

        return when (where.operator) {
            Operator.LIKE -> cb.like(
                getFieldFrom<String>(from, field),
                where.value.toString()
            )

            Operator.NOT_LIKE -> cb.notLike(
                getFieldFrom<String>(from, field),
                where.value.toString()
            )

            Operator.EQUAL -> cb.equal(
                getFieldFrom<Any>(from, field),
                where.value
            )

            Operator.DIFF -> cb.notEqual(
                getFieldFrom<Any>(from, field),
                where.value
            )

            Operator.GREATER -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                val value = getTransformedValue(where.value, path)
                @Suppress("UNCHECKED_CAST")
                cb.greaterThan(path as Path<Comparable<Any>>, value as Comparable<Any>)
            }

            Operator.LESS -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                val value = getTransformedValue(where.value, path)
                @Suppress("UNCHECKED_CAST")
                cb.lessThan(path as Path<Comparable<Any>>, value as Comparable<Any>)
            }

            Operator.GREATER_OR_EQUAL -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                val value = getTransformedValue(where.value, path)
                @Suppress("UNCHECKED_CAST")
                cb.greaterThanOrEqualTo(path as Path<Comparable<Any>>, value as Comparable<Any>)
            }

            Operator.LESS_OR_EQUAL -> {
                val path: Path<out Comparable<*>> = getFieldFrom(from, field)
                val value = getTransformedValue(where.value, path)
                @Suppress("UNCHECKED_CAST")
                cb.lessThanOrEqualTo(path as Path<Comparable<Any>>, value as Comparable<Any>)
            }

            Operator.IS_NULL -> cb.isNull(getFieldFrom<Any>(from, field))
            Operator.IS_NOT_NULL -> cb.isNotNull(getFieldFrom<Any>(from, field))

            Operator.IN -> applyWhereIn(where, from, field)
            Operator.NOT_IN -> cb.not(applyWhereIn(where, from, field))

            Operator.FIND_IN_SET -> cb.greaterThan(
                getPredicateForFindInSet(where, getFieldFrom(from, field)),
                cb.literal(0)
            )

            Operator.NOT_FIND_IN_SET -> cb.equal(
                getPredicateForFindInSet(where, getFieldFrom(from, field)),
                cb.literal(0)
            )
        }
    }

    private fun getPredicateForFindInSet(where: Where, path: Path<Any>): Expression<Int> {
        return cb.function("find_in_set", Int::class.java, cb.literal(where.value.toString()), path)
    }

    private fun applyWhereIn(where: Where, from: From<*, *>, field: String): Predicate {
        val inClause = cb.`in`(getFieldFrom<Any>(from, field))

        when (val value = where.value) {
            is Array<*> -> {
                value.forEach { inClause.value(it as Any) }
                return inClause
            }

            is Iterable<*> -> {
                value.forEach { inClause.value(it as Any) }
                return inClause
            }

            is Hefesto<*> -> {
                if (value.getSelectsSize() != 1) {
                    throw QueryException("The quantity of select for sub-query must be 1 for Where IN operation")
                }

                if (!value.hasCustomResultForSubQuery()) {
                    throw QueryException("The sub-query must have custom result for Where IN operation")
                }

                inClause.value(value.getSubQuery(cr!!, root, cb, joins))
                return inClause
            }

            else -> throw UnsupportedOperationException("Invalid value for Where IN operation")
        }
    }

    private fun applyWhereOperation(where: BaseWhere, vararg predicate: Predicate): Predicate {
        return when (where.whereOperation) {
            WhereOperator.OR -> cb.or(*predicate)
            WhereOperator.AND -> cb.and(*predicate)
        }
    }

    private fun getTransformedValue(originalValue: Any?, path: Path<out Comparable<*>>): Comparable<*> {
        val typeField = path.javaType
        val value = originalValue ?: throw IllegalArgumentException("Value cannot be null")
        return CastUtils.castValue(typeField, value) as Comparable<*>
    }

    fun constructSubQuery(subQuery: Subquery<*>, cb: CriteriaBuilder, root: Root<*>, parentRoot: Root<*>) {
        if (isEmpty()) {
            return
        }

        this.cb = cb
        // Don't set cr for subqueries - it will cause ClassCastException
        // WhereCustom lambdas in subqueries should handle null cr gracefully
        this.root = root
        this.parentRoot = parentRoot

        val predicate: Predicate = transform(items)
        @Suppress("UNCHECKED_CAST")
        (subQuery as Subquery<Any>).where(predicate)
    }
}
