package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.actions.wheres.BaseWhere
import io.github.robertomike.hefesto.actions.wheres.CollectionWhere
import io.github.robertomike.hefesto.actions.wheres.Where
import io.github.robertomike.hefesto.actions.wheres.WhereExist
import io.github.robertomike.hefesto.builders.BaseBuilder
import io.github.robertomike.hefesto.constructors.ConstructWhere
import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.enums.WhereOperator

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
     * Adds a BaseWhere object to the list of wheres and returns itself.
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
}
