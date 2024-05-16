package io.github.robertomike.hefesto.utils;

import io.github.robertomike.hefesto.actions.wheres.BaseWhere;
import io.github.robertomike.hefesto.actions.wheres.CollectionWhere;
import io.github.robertomike.hefesto.actions.wheres.Where;
import io.github.robertomike.hefesto.actions.wheres.WhereExist;
import io.github.robertomike.hefesto.builders.BaseBuilder;
import io.github.robertomike.hefesto.constructors.ConstructWhere;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.enums.WhereOperator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to build a WHERE clause.
 *
 * @param <R> is the builder
 */
@SuppressWarnings("unchecked")
public interface ConditionalBuilder<R extends ConditionalBuilder<R>> {

    /**
     * Returns the 'Constructor of Where' object.
     *
     * @return the 'Constructor of Where' object
     */
    ConstructWhere getWheres();

    /**
     * Adds a WHERE condition to check if the specified field is null.
     *
     * @param field the name of the field to check
     * @return the modified object of type R
     */
    default R whereIsNull(String field) {
        getWheres().add(new Where(field, Operator.IS_NULL));
        return (R) this;
    }

    /**
     * Adds a "where" clause to the query to check if the given field is not null.
     *
     * @param field the name of the field to check
     * @return the updated query object
     */
    default R whereIsNotNull(String field) {
        getWheres().add(new Where(field, Operator.IS_NOT_NULL));
        return (R) this;
    }

    /**
     * Adds a new OR condition to the list of WHERE conditions, where the specified field is null.
     *
     * @param field the field to check for null value
     * @return the updated instance of the class
     */
    default R orWhereIsNull(String field) {
        getWheres().add(new Where(field, Operator.IS_NULL, WhereOperator.OR));
        return (R) this;
    }

    /**
     * Add a WHERE clause to the query that checks if the value of the specified field is not null.
     *
     * @param field the name of the field to check
     * @return the modified query object
     */
    default R orWhereIsNotNull(String field) {
        getWheres().add(new Where(field, Operator.IS_NOT_NULL, WhereOperator.OR));
        return (R) this;
    }

    /**
     * Adds a list of WHERE conditions, this creates a CollectionWhere.
     * This method adds parenthesis to the where's inserted here.
     *
     * @param whereList the list of BaseWhere objects to be added
     * @return the updated object of type R
     */
    default R where(BaseWhere... whereList) {
        getWheres().add(
                new CollectionWhere(Stream.of(whereList).collect(Collectors.toList()))
        );
        return (R) this;
    }

    /**
     * Adds a list of WHERE conditions using the OR operator, this creates a CollectionWhere.
     * This method adds parenthesis to the where's inserted here.
     *
     * @param whereList a list of BaseWhere conditions to be added
     * @return the modified object of type R
     */
    default R orWhere(BaseWhere... whereList) {
        getWheres().add(
                new CollectionWhere(Stream.of(whereList).collect(Collectors.toList()), WhereOperator.OR)
        );
        return (R) this;
    }

    /**
     * Adds an OR condition to the query by specifying a field and a value.
     *
     * @param field the field to compare
     * @param value the value to compare against
     * @return the modified object
     */
    default R orWhere(String field, Object value) {
        if (value != null) {
            getWheres().add(new Where(field, Operator.EQUAL, value, WhereOperator.OR));
        }
        return (R) this;
    }

    /**
     * Adds an "OR" condition to the query by specifying the field, operator, and value.
     *
     * @param field    the field to compare against
     * @param operator the comparison operator
     * @param value    the value to compare with
     * @return the updated query object
     */
    default R orWhere(String field, Operator operator, Object value) {
        if (value != null) {
            getWheres().add(new Where(field, operator, value, WhereOperator.OR));
        }

        return (R) this;
    }

    /**
     * A description of the entire Java function.
     *
     * @param field    description of parameter
     * @param operator description of parameter
     * @param value    description of parameter
     * @return description of return value
     */
    default R where(String field, Operator operator, Object value) {
        if (value != null) {
            getWheres().add(new Where(field, operator, value));
        }
        return (R) this;
    }

    /**
     * Adds a where clause to the query if the value is not null.
     *
     * @param field the field to compare
     * @param value the value to compare against
     * @return the modified query object
     */
    default R where(String field, Object value) {
        if (value != null) {
            getWheres().add(new Where(field, Operator.EQUAL, value));
        }
        return (R) this;
    }

    /**
     * Adds a WHERE IN clause to the query.
     *
     * @param field  the field to apply the WHERE clause on
     * @param values the values to match against
     * @return the modified query object
     */
    default R whereIn(String field, String... values) {
        getWheres().add(new Where(field, Operator.IN, values));
        return (R) this;
    }

    /**
     * Adds a WHERE IN clause with a subQuery as value to the query.
     *
     * @param field    the field to apply the WHERE clause on
     * @param subQuery the subQuery for the WHERE clause
     * @return the modified query object
     */
    default R whereIn(String field, BaseBuilder<?, ?, ?, ?, ?, ?, ?> subQuery) {
        getWheres().add(new Where(field, Operator.IN, subQuery));
        return (R) this;
    }

    /**
     * Adds a WHERE NOT IN clause with a subQuery as value to the query.
     *
     * @param field    the field to apply the WHERE clause on
     * @param subQuery the subQuery for the WHERE clause
     * @return the modified query object
     */
    default R whereNotIn(String field, BaseBuilder<?, ?, ?, ?, ?, ?, ?> subQuery) {
        getWheres().add(new Where(field, Operator.NOT_IN, subQuery));
        return (R) this;
    }

    /**
     * Adds a WHERE IN clause to the query.
     *
     * @param field  the field to apply the WHERE clause on
     * @param values the values to match against
     * @return the modified query object
     */
    default R orWhereIn(String field, String... values) {
        getWheres().add(new Where(field, Operator.IN, values, WhereOperator.OR));
        return (R) this;
    }

    /**
     * Adds a WHERE IN clause with a subQuery as value to the query.
     *
     * @param field    the field to apply the WHERE clause on
     * @param subQuery the subQuery for the WHERE clause
     * @return the modified query object
     */
    default R orWhereIn(String field, BaseBuilder<?, ?, ?, ?, ?, ?, ?> subQuery) {
        getWheres().add(new Where(field, Operator.IN, subQuery, WhereOperator.OR));
        return (R) this;
    }

    /**
     * Adds a WHERE NOT IN clause with a subQuery as value to the query.
     *
     * @param field    the field to apply the WHERE clause on
     * @param subQuery the subQuery for the WHERE clause
     * @return the modified query object
     */
    default R orWhereNotIn(String field, BaseBuilder<?, ?, ?, ?, ?, ?, ?> subQuery) {
        getWheres().add(new Where(field, Operator.NOT_IN, subQuery, WhereOperator.OR));
        return (R) this;
    }

    /**
     * Adds a WHERE NOT IN condition to the query.
     *
     * @param field  the field to check against
     * @param values the values to check against
     * @return the modified query object
     */
    default R whereNotIn(String field, String... values) {
        getWheres().add(new Where(field, Operator.NOT_IN, values));
        return (R) this;
    }

    /**
     * Adds a list of WHERE conditions, this creates a CollectionWhere.
     * This method adds parenthesis to the where's inserted here.
     *
     * @param whereList the list of BaseWhere objects
     * @return the result of the function
     */
    default R where(List<BaseWhere> whereList) {
        getWheres().add(new CollectionWhere(whereList));
        return (R) this;
    }

    /**
     * Adds a BaseWhere object to the list of wheres and returns itself.
     *
     * @param where the BaseWhere object to be added
     * @return the modified object
     */
    default R where(BaseWhere where) {
        getWheres().add(where);
        return (R) this;
    }

    /**
     * A method to add a sub-query as a condition to the current query where need to exist.
     *
     * @param subQuery the sub-query to be added as a condition
     * @return the current builder instance
     */
    default R whereExists(BaseBuilder<?, ?, ?, ?, ?, ?, ?> subQuery) {
        getWheres().add(new WhereExist(subQuery));
        return (R) this;
    }

    /**
     * A method to add a sub-query as a condition to the current query where not need exist.
     *
     * @param subQuery the sub-query to check for non-existence
     * @return the updated query builder
     */
    default R whereNotExists(BaseBuilder<?, ?, ?, ?, ?, ?, ?> subQuery) {
        getWheres().add(new WhereExist(false, subQuery));
        return (R) this;
    }

    /**
     * A method to add a sub-query as a condition to the current query where need to exist.
     *
     * @param subQuery the sub-query to be added as a condition
     * @return the current builder instance
     */
    default R orWhereExists(BaseBuilder<?, ?, ?, ?, ?, ?, ?> subQuery) {
        getWheres().add(new WhereExist(subQuery, WhereOperator.OR));
        return (R) this;
    }

    /**
     * A method to add a sub-query as a condition to the current query where not need exist.
     *
     * @param subQuery the sub-query to check for non-existence
     * @return the updated query builder
     */
    default R orWhereNotExists(BaseBuilder<?, ?, ?, ?, ?, ?, ?> subQuery) {
        getWheres().add(new WhereExist(false, subQuery, WhereOperator.OR));
        return (R) this;
    }
}
