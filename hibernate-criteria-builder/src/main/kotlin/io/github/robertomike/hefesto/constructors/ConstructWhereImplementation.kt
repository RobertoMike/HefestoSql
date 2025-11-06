package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.wheres.*
import io.github.robertomike.hefesto.builders.Hefesto
import io.github.robertomike.hefesto.exceptions.QueryException
import io.github.robertomike.hefesto.exceptions.UnsupportedOperationException
import io.github.robertomike.hefesto.utils.CastUtils
import io.github.robertomike.hefesto.utils.HibernateUtils.DOT_REGEX
import io.github.robertomike.hefesto.utils.HibernateUtils.getFieldFrom
import jakarta.persistence.criteria.*

class ConstructWhereImplementation : ConstructWhere() {
    protected lateinit var cb: CriteriaBuilder
    protected lateinit var cr: CriteriaQuery<*>
    protected lateinit var root: Root<*>
    private var joins: Map<String, Join<*, *>> = HashMap()
    private var parentRoot: Root<*>? = null

    fun setJoins(joins: Map<String, Join<*, *>>): ConstructWhereImplementation {
        this.joins = joins
        return this
    }

    fun construct(cb: CriteriaBuilder, cr: CriteriaQuery<*>, root: Root<*>) {
        if (isEmpty()) {
            return
        }

        this.cr = cr
        this.cb = cb
        this.root = root

        cr.where(transform(items))
    }

    private fun transform(wheres: List<BaseWhere>): Predicate {
        var lastPredicate: Predicate? = null

        wheres.forEach { value ->
            val predicate = getPredicateFromWhere(value)

            if (lastPredicate == null) {
                lastPredicate = predicate
                return@forEach
            }

            lastPredicate = applyWhereOperation(value, lastPredicate!!, predicate)
        }

        return lastPredicate!!
    }

    fun getPredicateFromWhere(where: BaseWhere): Predicate {
        return when (where) {
            is CollectionWhere -> transform(where.wheres)
            is WhereCustom -> where.custom.call(cb, cr, root, joins, parentRoot!!)
            is WhereExist -> applyWhereExist(where)
            is WhereField -> constructWhereField(where)
            is Where -> constructWhere(where)
            else -> throw QueryException("Invalid class extended from BaseWhere: ${where.javaClass}")
        }
    }

    private fun constructWhereField(where: WhereField): Predicate {
        var from: From<*, *> = root
        var parentFrom: From<*, *> = parentRoot ?: root
        var parentField = where.parentField
        var field = where.field

        if (field.contains(".") && joins.containsKey(field.split(DOT_REGEX.toRegex())[0])) {
            val split = field.split(DOT_REGEX.toRegex())
            from = joins[split[0]]!!
            field = split[1]
        }

        if (parentField!!.contains(".") && joins.containsKey(field.split(DOT_REGEX.toRegex())[0])) {
            val split = parentField.split(DOT_REGEX.toRegex())
            parentFrom = joins[split[0]]!!
            parentField = split[1]
        }

        return when (where.operator) {
            io.github.robertomike.hefesto.enums.Operator.LIKE -> cb.like(
                getFieldFrom<String>(from, field),
                getFieldFrom<String>(parentFrom, parentField)
            )

            io.github.robertomike.hefesto.enums.Operator.NOT_LIKE -> cb.notLike(
                getFieldFrom<String>(from, field),
                getFieldFrom<String>(parentFrom, parentField)
            )

            io.github.robertomike.hefesto.enums.Operator.EQUAL -> cb.equal(
                getFieldFrom<Any>(from, field),
                getFieldFrom<Any>(parentFrom, parentField)
            )

            io.github.robertomike.hefesto.enums.Operator.DIFF -> cb.notEqual(
                getFieldFrom<Any>(from, field),
                getFieldFrom<Any>(parentFrom, parentField)
            )

            io.github.robertomike.hefesto.enums.Operator.GREATER -> cb.gt(
                getFieldFrom<Number>(from, field),
                getFieldFrom<Number>(parentFrom, parentField)
            )

            io.github.robertomike.hefesto.enums.Operator.LESS -> cb.lt(
                getFieldFrom<Number>(from, field),
                getFieldFrom<Number>(parentFrom, parentField)
            )

            io.github.robertomike.hefesto.enums.Operator.GREATER_OR_EQUAL -> cb.ge(
                getFieldFrom<Number>(from, field),
                getFieldFrom<Number>(parentFrom, parentField)
            )

            io.github.robertomike.hefesto.enums.Operator.LESS_OR_EQUAL -> cb.le(
                getFieldFrom<Number>(from, field),
                getFieldFrom<Number>(parentFrom, parentField)
            )

            else -> throw UnsupportedOperationException("Unsupported operator: ${where.operator}")
        }
    }

    private fun applyWhereExist(whereExist: WhereExist): Predicate {
        val subBuilder = whereExist.subQuery as Hefesto<*>
        val subQuery = subBuilder.getSubQuery(cr, root, cb, joins)

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
            io.github.robertomike.hefesto.enums.Operator.LIKE -> cb.like(
                getFieldFrom<String>(from, field),
                where.value.toString()
            )

            io.github.robertomike.hefesto.enums.Operator.NOT_LIKE -> cb.notLike(
                getFieldFrom<String>(from, field),
                where.value.toString()
            )

            io.github.robertomike.hefesto.enums.Operator.EQUAL -> cb.equal(
                getFieldFrom<Any>(from, field),
                where.value
            )

            io.github.robertomike.hefesto.enums.Operator.DIFF -> cb.notEqual(
                getFieldFrom<Any>(from, field),
                where.value
            )

            io.github.robertomike.hefesto.enums.Operator.GREATER -> {
                val path: Path<Number> = getFieldFrom(from, field)
                val value = getTransformedValue(where.value, path)
                cb.gt(path, value)
            }

            io.github.robertomike.hefesto.enums.Operator.LESS -> {
                val path: Path<Number> = getFieldFrom(from, field)
                val value = getTransformedValue(where.value, path)
                cb.lt(path, value)
            }

            io.github.robertomike.hefesto.enums.Operator.GREATER_OR_EQUAL -> {
                val path: Path<Number> = getFieldFrom(from, field)
                val value = getTransformedValue(where.value, path)
                cb.ge(path, value)
            }

            io.github.robertomike.hefesto.enums.Operator.LESS_OR_EQUAL -> {
                val path: Path<Number> = getFieldFrom(from, field)
                val value = getTransformedValue(where.value, path)
                cb.le(path, value)
            }

            io.github.robertomike.hefesto.enums.Operator.IS_NULL -> cb.isNull(getFieldFrom<Any>(from, field))
            io.github.robertomike.hefesto.enums.Operator.IS_NOT_NULL -> cb.isNotNull(getFieldFrom<Any>(from, field))

            io.github.robertomike.hefesto.enums.Operator.IN -> applyWhereIn(where, from, field)
            io.github.robertomike.hefesto.enums.Operator.NOT_IN -> cb.not(applyWhereIn(where, from, field))

            io.github.robertomike.hefesto.enums.Operator.FIND_IN_SET -> cb.greaterThan(
                getPredicateForFindInSet(where, getFieldFrom(from, field)),
                cb.literal(0)
            )

            io.github.robertomike.hefesto.enums.Operator.NOT_FIND_IN_SET -> cb.equal(
                getPredicateForFindInSet(where, getFieldFrom(from, field)),
                cb.literal(0)
            )

            else -> throw UnsupportedOperationException("Unsupported operator: ${where.operator}")
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

                inClause.value(value.getSubQuery(cr, root, cb, joins))
                return inClause
            }

            else -> throw UnsupportedOperationException("Invalid value for Where IN operation")
        }
    }

    private fun applyWhereOperation(where: BaseWhere, vararg predicate: Predicate): Predicate {
        return when (where.whereOperation) {
            io.github.robertomike.hefesto.enums.WhereOperator.OR -> cb.or(*predicate)
            io.github.robertomike.hefesto.enums.WhereOperator.AND -> cb.and(*predicate)
            else -> throw UnsupportedOperationException("Unsupported where operation: ${where.whereOperation}")
        }
    }

    private fun getTransformedValue(originalValue: Any?, path: Path<Number>): Number {
        val typeField = path.javaType
        val value = originalValue ?: throw IllegalArgumentException("Value cannot be null")
        val casted = CastUtils.castValue(typeField, value)
        return casted as Number
    }

    fun constructSubQuery(subQuery: Subquery<*>, cb: CriteriaBuilder, root: Root<*>, parentRoot: Root<*>) {
        if (isEmpty()) {
            return
        }

        this.cb = cb
        this.root = root
        this.parentRoot = parentRoot

        subQuery.where(transform(items))
    }
}
