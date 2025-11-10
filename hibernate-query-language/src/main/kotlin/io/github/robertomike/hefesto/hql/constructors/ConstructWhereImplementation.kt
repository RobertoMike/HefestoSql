package io.github.robertomike.hefesto.hql.constructors

import io.github.robertomike.hefesto.actions.wheres.*
import io.github.robertomike.hefesto.hql.builders.Hefesto
import io.github.robertomike.hefesto.constructors.ConstructWhere
import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.exceptions.QueryException
import io.github.robertomike.hefesto.exceptions.UnsupportedOperationException
import io.github.robertomike.hefesto.hql.actions.wheres.WhereRaw

/**
 * HQL implementation of WHERE clause construction.
 * 
 * Converts WHERE conditions into HQL string-based query fragments.
 * Manages query parameters to prevent SQL injection.
 * 
 * Supports:
 * - All standard operators (EQUAL, GREATER, LIKE, etc.)
 * - Collection operations (IN, FIND_IN_SET)
 * - NULL checks
 * - Nested condition groups with AND/OR operators
 * - Raw HQL conditions
 * - Subquery conditions
 * 
 * Parameters are stored in a map and referenced by placeholder names
 * in the generated HQL string.
 */
class ConstructWhereImplementation : ConstructWhere() {
    private lateinit var params: MutableMap<String, Any?>
    private var acronymTable: String = ""

    /**
     * Constructs the WHERE clause as an HQL string.
     * Builds parameter map and generates WHERE conditions with proper operators.
     *
     * @param params mutable map to store query parameters
     * @param acronymTable the table alias to use for field references
     * @return the HQL WHERE clause string (e.g., "Where user.name = :param1 AND user.age > :param2")
     */
    fun construct(params: MutableMap<String, Any?>, acronymTable: String = ""): String {
        this.params = params
        this.acronymTable = acronymTable

        if (items.isEmpty()) {
            return ""
        }
        val wheresQuery = mutableListOf<String>()

        iterator(wheresQuery, items)

        if (wheresQuery.isNotEmpty()) {
            wheresQuery.add(0, "Where")
        }

        return wheresQuery.joinToString(" ")
    }

    fun iterator(wheresQuery: MutableList<String>, whereList: List<BaseWhere>) {
        whereList.forEach { value ->
            if (value is CollectionWhere) {
                if (wheresQuery.isNotEmpty() && wheresQuery[wheresQuery.size - 1] != "(") {
                    wheresQuery.add(" ${value.whereOperation.operator}")
                }

                wheresQuery.add("(")
                iterator(wheresQuery, value.wheres)
                wheresQuery.add(")")
                return@forEach
            }
            constructBaseWhere(wheresQuery, value)
        }
    }

    fun constructBaseWhere(wheresQuery: MutableList<String>, where: BaseWhere) {
        if (wheresQuery.isNotEmpty() && wheresQuery[wheresQuery.size - 1] != "(") {
            wheresQuery.add(where.whereOperation.operator)
        }

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

    /**
     * Constructs HQL for comparing two fields.
     * Automatically qualifies field names with table aliases.
     *
     * @param wheresQuery the list to append the WHERE clause to
     * @param whereField the WhereField condition containing the two fields to compare
     */
    private fun constructWhereField(wheresQuery: MutableList<String>, whereField: WhereField) {
        val field1 = qualifyFieldName(whereField.field)
        val field2 = qualifyFieldName(whereField.secondField)
        
        when (whereField.operator) {
            Operator.LIKE,
            Operator.NOT_LIKE,
            Operator.EQUAL,
            Operator.DIFF,
            Operator.GREATER,
            Operator.GREATER_OR_EQUAL,
            Operator.LESS,
            Operator.LESS_OR_EQUAL ->
                wheresQuery.add("$field1 ${whereField.operator.operator} $field2")

            else -> throw UnsupportedOperationException("Unsupported operator for field comparison: ${whereField.operator}")
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
        val field = qualifyFieldName(where.field)
        val nameParam = standardizeNameParam(where.field, wheresQuery.size)
        val nameParamWhere = standardizeNameParamWhere(nameParam)
        val operator = where.operator.operator
        val value = where.value

        var param = true

        when (where.operator) {
            Operator.IN,
            Operator.NOT_IN -> {
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

            Operator.IS_NULL,
            Operator.IS_NOT_NULL -> {
                wheresQuery.add("$field $operator")
                param = false
            }

            Operator.NOT_FIND_IN_SET ->
                wheresQuery.add("$operator($nameParamWhere,$field) = 0")

            Operator.FIND_IN_SET ->
                wheresQuery.add("$operator($nameParamWhere,$field) > 0")

            else -> wheresQuery.add("$field $operator $nameParamWhere")
        }

        if (param) {
            params[nameParam] = value
        }
    }

    private fun qualifyFieldName(field: String): String {
        // If the field already contains a dot (e.g., "user.id") or if there's no acronym, return as is
        if (field.contains(".") || acronymTable.isEmpty()) {
            return field
        }
        // Otherwise, prepend the acronym (e.g., "id" becomes "user.id")
        return "$acronymTable.$field"
    }
}
