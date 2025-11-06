package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.actions.Join
import io.github.robertomike.hefesto.actions.JoinFetch
import io.github.robertomike.hefesto.actions.wheres.WhereField
import io.github.robertomike.hefesto.builders.BaseBuilder
import io.github.robertomike.hefesto.constructors.Construct
import io.github.robertomike.hefesto.constructors.ConstructJoin
import io.github.robertomike.hefesto.constructors.ConstructWhere
import io.github.robertomike.hefesto.enums.JoinOperator
import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.models.BaseModel
import jakarta.persistence.criteria.JoinType

/**
 * This class is used to build a WHERE clause.
 *
 * @param <B> is the builder
 */
@Suppress("UNCHECKED_CAST")
interface SharedMethods<B : BaseBuilder<*, *, *, *, *, *, *, *>> {

    val joins: ConstructJoin
    val wheres: ConstructWhere
    val joinsFetch: Construct<JoinFetch>

    /**
     * Returns the model.
     *
     * @return the model
     */
    val model: Class<out BaseModel>

    /**
     * Adds a join to load in the query.
     *
     * @param relationship the relationship to join on
     * @return the updated Hefesto object
     */
    fun join(relationship: String): B {
        joins.add(Join.make(relationship))
        return this as B
    }

    /**
     * Adds a join to load in the query.
     *
     * @param relationship the relationship to join on
     * @return the updated Hefesto object
     */
    fun join(relationship: String, alias: String): B {
        joins.add(Join(relationship, alias, null, null))
        return this as B
    }

    /**
     * Adds a join to load in the query.
     *
     * @param operator the join operator
     * @return the updated Hefesto object
     */
    fun join(relationship: String, operator: JoinOperator): B {
        joins.add(Join.make(relationship, operator))
        return this as B
    }

    /**
     * This method adds a where field to the query that allows you to filter by a field of a join, the same root or the parent when is a subQuery
     *
     * @param parentField could be parent field or a join field
     */
    fun whereField(field: String, parentField: String): B {
        wheres.add(WhereField(field, parentField))
        return this as B
    }

    /**
     * This method adds a where field to the query that allows you to filter by a field of a join, the same root or the parent when is a subQuery
     *
     * @param operator    not all operators are supported, only LIKE, NOT_LIKE, EQUAL, DIFF, GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL
     * @param parentField could be parent field or a join field
     */
    fun whereField(field: String, operator: Operator, parentField: String): B {
        wheres.add(WhereField(field, operator, parentField))
        return this as B
    }

    /**
     * This method will allow you to preload the relationships you want to fetch
     */
    fun with(vararg relationship: JoinFetch): B {
        joinsFetch.addAll(*relationship)
        return this as B
    }

    /**
     * This method will allow you to preload the relationships you want to fetch
     */
    fun with(vararg relationships: String): B {
        for (relationship in relationships) {
            joinsFetch.add(JoinFetch.make(relationship))
        }
        return this as B
    }

    /**
     * This method will allow you to preload the relationships you want to fetch with the join type
     */
    fun with(relationship: String, joinType: JoinType): B {
        joinsFetch.add(JoinFetch.make(relationship, joinType))
        return this as B
    }
}
