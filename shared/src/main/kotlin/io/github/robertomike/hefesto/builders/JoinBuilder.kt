package io.github.robertomike.hefesto.builders

import io.github.robertomike.hefesto.actions.Join
import io.github.robertomike.hefesto.actions.wheres.Where
import io.github.robertomike.hefesto.enums.JoinOperator
import io.github.robertomike.hefesto.enums.Operator
import java.util.function.Consumer

/**
 * Builder for configuring joins with inline conditions and nested joins.
 * Allows setting alias, adding WHERE conditions, and creating nested/deep joins.
 * 
 * Example - Simple join with conditions:
 * ```java
 * Hefesto.make(User.class)
 *     .join("pets", join -> {
 *         join.alias("Pet");
 *         join.where("name", "lola");
 *     })
 *     .get();
 * ```
 * 
 * Example - Nested joins:
 * ```java
 * Hefesto.make(User.class)
 *     .join("posts", posts -> {
 *         posts.join("comments", comments -> {
 *             comments.where("approved", true);
 *         });
 *         posts.where("published", true);
 *     })
 *     .get();
 * ```
 */
class JoinBuilder(table: String, private val joinOperator: JoinOperator = JoinOperator.INNER) {
    private var aliasValue: String? = null
    private var joinInstance: Join = Join(table, joinOperator = joinOperator)
    
    /**
     * Sets an alias for this join to use in WHERE/SELECT clauses.
     * 
     * @param alias the alias name
     * @return this builder for chaining
     */
    fun alias(alias: String): JoinBuilder {
        this.aliasValue = alias
        joinInstance.alias = alias
        return this
    }
    
    /**
     * Adds a WHERE condition on the joined table.
     * The field will be resolved relative to the joined table, not the root entity.
     * 
     * @param field the field name to filter on
     * @param value the value to compare against
     * @param operator the comparison operator (default: EQUAL)
     * @return this builder for chaining
     */
    @JvmOverloads
    fun where(field: String, value: Any?, operator: Operator = Operator.EQUAL): JoinBuilder {
        joinInstance.conditions.add(Where(field, operator, value))
        return this
    }
    
    /**
     * Creates a nested INNER join from this join.
     * This allows creating deep join paths like: User → Posts → Comments
     * 
     * Example:
     * ```java
     * .join("posts", posts -> {
     *     posts.join("comments");
     * })
     * ```
     * 
     * @param table the table/relationship name to join
     * @return a new JoinBuilder for the nested join
     */
    fun join(table: String): JoinBuilder {
        return join(table, JoinOperator.INNER)
    }
    
    /**
     * Creates a nested join from this join with a specific join type.
     * 
     * @param table the table/relationship name to join
     * @param joinType the type of join (INNER, LEFT, RIGHT)
     * @return a new JoinBuilder for the nested join
     */
    fun join(table: String, joinType: JoinOperator): JoinBuilder {
        val nestedJoin = Join(table, joinOperator = joinType)
        joinInstance.deepJoins.add(nestedJoin)
        return JoinBuilder(table, joinType).apply {
            this.joinInstance = nestedJoin
        }
    }
    
    /**
     * Creates a nested join with a configuration lambda.
     * This is the most flexible way to create nested joins with conditions.
     * 
     * Example:
     * ```java
     * .join("posts", posts -> {
     *     posts.join("comments", comments -> {
     *         comments.where("approved", true);
     *         comments.alias("ApprovedComments");
     *     });
     *     posts.where("published", true);
     * })
     * ```
     * 
     * @param table the table/relationship name to join
     * @param configurator lambda to configure the nested join
     * @return this builder for chaining
     */
    fun join(table: String, configurator: Consumer<JoinBuilder>): JoinBuilder {
        return join(table, JoinOperator.INNER, configurator)
    }
    
    /**
     * Creates a nested join with a specific join type and configuration lambda.
     * 
     * @param table the table/relationship name to join
     * @param joinType the type of join (INNER, LEFT, RIGHT)
     * @param configurator lambda to configure the nested join
     * @return this builder for chaining
     */
    fun join(table: String, joinType: JoinOperator, configurator: Consumer<JoinBuilder>): JoinBuilder {
        val nestedJoin = Join(table, joinOperator = joinType)
        joinInstance.deepJoins.add(nestedJoin)
        
        val nestedBuilder = JoinBuilder(table, joinType)
        nestedBuilder.joinInstance = nestedJoin
        configurator.accept(nestedBuilder)
        
        return this
    }
    
    /**
     * Creates a LEFT join from this join.
     * 
     * @param table the table/relationship name to join
     * @return a new JoinBuilder for the nested join
     */
    fun leftJoin(table: String): JoinBuilder {
        return join(table, JoinOperator.LEFT)
    }
    
    /**
     * Creates a LEFT join with a configuration lambda.
     * 
     * @param table the table/relationship name to join
     * @param configurator lambda to configure the nested join
     * @return this builder for chaining
     */
    fun leftJoin(table: String, configurator: Consumer<JoinBuilder>): JoinBuilder {
        return join(table, JoinOperator.LEFT, configurator)
    }
    
    /**
     * Creates a RIGHT join from this join.
     * 
     * @param table the table/relationship name to join
     * @return a new JoinBuilder for the nested join
     */
    fun rightJoin(table: String): JoinBuilder {
        return join(table, JoinOperator.RIGHT)
    }
    
    /**
     * Creates a RIGHT join with a configuration lambda.
     * 
     * @param table the table/relationship name to join
     * @param configurator lambda to configure the nested join
     * @return this builder for chaining
     */
    fun rightJoin(table: String, configurator: Consumer<JoinBuilder>): JoinBuilder {
        return join(table, JoinOperator.RIGHT, configurator)
    }
    
    /**
     * Creates an INNER join from this join.
     * 
     * @param table the table/relationship name to join
     * @return a new JoinBuilder for the nested join
     */
    fun innerJoin(table: String): JoinBuilder {
        return join(table, JoinOperator.INNER)
    }
    
    /**
     * Creates an INNER join with a configuration lambda.
     * 
     * @param table the table/relationship name to join
     * @param configurator lambda to configure the nested join
     * @return this builder for chaining
     */
    fun innerJoin(table: String, configurator: Consumer<JoinBuilder>): JoinBuilder {
        return join(table, JoinOperator.INNER, configurator)
    }
    
    /**
     * Builds the Join object with configured settings.
     * This is internal and should not be called by users directly.
     * 
     * @return the configured Join object
     */
    fun build(): Join {
        return joinInstance
    }
}
