package io.github.robertomike.hefesto.utils;

import io.github.robertomike.hefesto.actions.Join;
import io.github.robertomike.hefesto.actions.JoinFetch;
import io.github.robertomike.hefesto.actions.wheres.WhereField;
import io.github.robertomike.hefesto.builders.BaseBuilder;
import io.github.robertomike.hefesto.constructors.Construct;
import io.github.robertomike.hefesto.constructors.ConstructJoin;
import io.github.robertomike.hefesto.constructors.ConstructWhere;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.models.BaseModel;

import javax.persistence.criteria.JoinType;

/**
 * This class is used to build a WHERE clause.
 *
 * @param <B> is the builder
 */
@SuppressWarnings("unchecked")
public interface SharedMethods<B extends BaseBuilder<?, ?, ?, ?, ?, ?, ?, ?>> {

    /**
     * Returns the 'Construct for Joins' object.
     *
     * @return the 'Construct for Joins' object
     */
    ConstructJoin getJoins();

    /**
     * Returns the 'Construct for Where' object.
     *
     * @return the 'Construct for Where' object
     */
    ConstructWhere getWheres();

    /**
     * Returns the 'Construct for JoinsFetch' object.
     *
     * @return the 'Construct for JoinsFetch' object
     */
    Construct<JoinFetch> getJoinsFetch();

    /**
     * Returns the model.
     *
     * @return the model
     */
    Class<? extends BaseModel> getModel();

    /**
     * Adds a join to load in the query.
     *
     * @param relationship the relationship to join on
     * @return the updated Hefesto object
     */
    default B join(String relationship) {
        getJoins().add(Join.make(relationship));
        return (B) this;
    }

    /**
     * Adds a join to load in the query.
     *
     * @param relationship the relationship to join on
     * @return the updated Hefesto object
     */
    default B join(String relationship, String alias) {
        getJoins().add(new Join(relationship, null, null, alias));
        return (B) this;
    }

    /**
     * Adds a join to load in the query.
     *
     * @param operator the join operator
     * @return the updated Hefesto object
     */
    default B join(String relationship, JoinOperator operator) {
        getJoins().add(Join.make(relationship, operator));
        return (B) this;
    }

    /**
     * This method adds a where field to the query that allows you to filter by a field of a join, the same root or the parent when is a subQuery
     *
     * @param parentField could be parent field or a join field
     */
    default B whereField(String field, String parentField) {
        getWheres().add(new WhereField(field, parentField));
        return (B) this;
    }

    /**
     * This method adds a where field to the query that allows you to filter by a field of a join, the same root or the parent when is a subQuery
     *
     * @param operator    not all operators are supported, only LIKE, NOT_LIKE, EQUAL, DIFF, GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL
     * @param parentField could be parent field or a join field
     */
    default B whereField(String field, Operator operator, String parentField) {
        getWheres().add(new WhereField(field, operator, parentField));
        return (B) this;
    }


    /**
     * This method will allow you to preload the relationships you want to fetch
     */
    default B with(JoinFetch... relationship) {
        getJoinsFetch().addAll(relationship);
        return (B) this;
    }

    /**
     * This method will allow you to preload the relationships you want to fetch
     */
    default B with(String... relationships) {
        for (var relationship : relationships) {
            getJoinsFetch().add(JoinFetch.make(relationship));
        }
        return (B) this;
    }

    /**
     * This method will allow you to preload the relationships you want to fetch with the join type
     */
    default B with(String relationship, JoinType joinType) {
        getJoinsFetch().add(JoinFetch.make(relationship, joinType));
        return (B) this;
    }
}
