package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.wheres.*
import io.github.robertomike.hefesto.builders.Hefesto
import io.github.robertomike.hefesto.exceptions.QueryException
import io.github.robertomike.hefesto.exceptions.UnsupportedOperationException

class ConstructWhereImplementation : ConstructWhere() {
    private lateinit var params: MutableMap<String, Any?>

    fun construct(params: MutableMap<String, Any?>): String {
        this.params = params

        if (items.isEmpty()) {
            return ""
        }
        val wheresQuery = mutableListOf<String>()

        iterator(wheresQuery, items)

        if (wheresQuery.isNotEmpty()) {
            wheresQuery.add(0, "Where ")
        }

        return wheresQuery.joinToString("")
    }

    fun iterator(wheresQuery: MutableList<String>, whereList: List<BaseWhere>) {
        whereList.forEach { value ->
            if (value is CollectionWhere) {
                if (wheresQuery.isNotEmpty()) {
                    wheresQuery.add(" ${value.whereOperation.operator}")
                }

                wheresQuery.add(" (")
                iterator(wheresQuery, value.wheres)
                wheresQuery.add(")")
                return@forEach
            }
            constructBaseWhere(wheresQuery, value)
        }
    }

    fun constructBaseWhere(wheresQuery: MutableList<String>, where: BaseWhere) {
        if (wheresQuery.isNotEmpty()) {
            if (wheresQuery[wheresQuery.size - 1] != " (") {
                wheresQuery.add(" ${where.whereOperation.operator}")
            }
        }

        wheresQuery.add(" ")

        if (where is WhereRaw) {
            wheresQuery.add(where.query)
            return
        }

        if (where is WhereExist) {
            wheresQuery.add(if (where.exists) "exists" else "not exists")
            wheresQuery.add("(")
            wheresQuery.add((where.subQuery as Hefesto<*>).getSubQuery(params))
            wheresQuery.add(")")
            return
        }

        if (where is WhereField) {
            constructWhereField(wheresQuery, where)
            return
        }

        if (where is Where) {
            constructWhere(wheresQuery, where)
            return
        }

        throw QueryException("Invalid class extended from BaseWhere: ${where.javaClass}")
    }

    private fun constructWhereField(wheresQuery: MutableList<String>, whereField: WhereField) {
        when (whereField.operator) {
            io.github.robertomike.hefesto.enums.Operator.LIKE,
            io.github.robertomike.hefesto.enums.Operator.NOT_LIKE,
            io.github.robertomike.hefesto.enums.Operator.EQUAL,
            io.github.robertomike.hefesto.enums.Operator.DIFF,
            io.github.robertomike.hefesto.enums.Operator.GREATER_OR_EQUAL,
            io.github.robertomike.hefesto.enums.Operator.LESS,
            io.github.robertomike.hefesto.enums.Operator.LESS_OR_EQUAL ->
                wheresQuery.add("${whereField.parentField} ${whereField.operator.operator} ${whereField.field}")

            else -> throw UnsupportedOperationException("Unsupported operator: ${whereField.operator}")
        }
    }

    fun standardizeNameParam(field: String, size: Int): String {
        return field.replace(".", "")
            .replace("(", "_")
            .replace(")", "_") + "_$size"
    }

    fun standardizeNameParamWhere(nameParam: String): String {
        return ":$nameParam"
    }

    fun constructWhere(wheresQuery: MutableList<String>, where: Where) {
        val field = where.field
        val nameParam = standardizeNameParam(field, wheresQuery.size)
        val nameParamWhere = standardizeNameParamWhere(nameParam)
        val operator = where.operator.operator
        val value = where.value

        var param = true

        when (where.operator) {
            io.github.robertomike.hefesto.enums.Operator.IN,
            io.github.robertomike.hefesto.enums.Operator.NOT_IN -> {
                if (where.value is Hefesto<*>) {
                    wheresQuery.add("$field $operator (${(where.value as Hefesto<*>).getSubQuery(params)})")
                    return
                }

                wheresQuery.add("$field $operator ($nameParamWhere)")

                val values: Any = when {
                    value?.javaClass?.isArray == true -> (value as Array<*>).toList()
                    value is Collection<*> -> ArrayList(value)
                    where.value is Hefesto<*> -> (where.value as Hefesto<*>).getSubQuery(params)
                    else -> throw UnsupportedOperationException("Invalid class: ${value?.javaClass}")
                }

                params[nameParam] = values
                param = false
            }

            io.github.robertomike.hefesto.enums.Operator.IS_NULL,
            io.github.robertomike.hefesto.enums.Operator.IS_NOT_NULL -> {
                wheresQuery.add("$field $operator")
                param = false
            }

            io.github.robertomike.hefesto.enums.Operator.NOT_FIND_IN_SET ->
                wheresQuery.add("$operator($nameParamWhere,$field) = 0")

            io.github.robertomike.hefesto.enums.Operator.FIND_IN_SET ->
                wheresQuery.add("$operator($nameParamWhere,$field) > 0")

            else -> wheresQuery.add("$field $operator $nameParamWhere")
        }

        if (param) {
            params[nameParam] = value
        }
    }
}
