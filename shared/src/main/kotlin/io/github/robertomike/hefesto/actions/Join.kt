package io.github.robertomike.hefesto.actions

import io.github.robertomike.hefesto.enums.JoinOperator
import java.util.*

data class Join(
    val table: String,
    var alias: String? = null,
    var fieldJoin: String? = null,
    var fieldReference: String? = null,
    var joinOperator: JoinOperator = JoinOperator.INNER
) {
    /**
     * Collection of nested/deep joins that should be applied on top of this join
     * Example: If this is a "store" join, you can add a "user" join to deepJoins
     * to create: root -> store -> user
     */
    val deepJoins: MutableList<Join> = mutableListOf()

    fun getAcronym(): String = alias ?: table.lowercase(Locale.ROOT)

    /**
     * Checks if this is a custom join (with explicit field conditions)
     * or a relationship join (just following entity relationships)
     */
    fun isCustomJoin(): Boolean = fieldJoin != null && fieldReference != null

    /**
     * Checks if this join has nested/deep joins
     */
    fun hasDeepJoins(): Boolean = deepJoins.isNotEmpty()

    /**
     * Adds a nested join to this join and returns the newly created nested join.
     * This allows for fluent chaining of deep joins.
     * 
     * Example:
     * ```
     * val storeJoin = Join("store")
     * storeJoin.withDeep("users")
     *          .withDeep("brands", JoinOperator.LEFT)
     * // Creates: store -> users -> brands
     * ```
     * 
     * @param table the table/relationship name for the nested join
     * @param operator the join operator (default: INNER)
     * @return the newly created nested join for further chaining
     */
    @JvmOverloads
    fun withDeep(table: String, operator: JoinOperator = JoinOperator.INNER): Join {
        val nestedJoin = Join(table, joinOperator = operator)
        deepJoins.add(nestedJoin)
        return nestedJoin
    }

    /**
     * Adds a nested join with an alias to this join and returns the newly created nested join.
     * 
     * @param table the table/relationship name for the nested join
     * @param alias the alias for the nested join
     * @param operator the join operator (default: INNER)
     * @return the newly created nested join for further chaining
     */
    @JvmOverloads
    fun withDeep(table: String, alias: String, operator: JoinOperator = JoinOperator.INNER): Join {
        val nestedJoin = Join(table, alias = alias, joinOperator = operator)
        deepJoins.add(nestedJoin)
        return nestedJoin
    }

    companion object {
        @JvmStatic
        fun make(table: String) = Join(table)

        /**
         * Creates a join with an alias (for use in where/select clauses)
         */
        @JvmStatic
        @JvmName("makeWithAlias")
        fun make(table: String, alias: String) = Join(table, alias = alias)

        @JvmStatic
        fun make(table: String, operator: JoinOperator) = Join(table, joinOperator = operator)

        /**
         * Creates a join with alias and operator
         */
        @JvmStatic
        @JvmName("makeWithAliasAndOperator")
        fun make(table: String, alias: String, operator: JoinOperator) = Join(table, alias = alias, joinOperator = operator)

        @JvmStatic
        fun make(table: String, fieldJoin: String, fieldReference: String) =
            Join(table, fieldJoin = fieldJoin, fieldReference = fieldReference)

        @JvmStatic
        fun make(table: String, fieldJoin: String, fieldReference: String, operator: JoinOperator) =
            Join(table, fieldJoin = fieldJoin, fieldReference = fieldReference, joinOperator = operator)

        @JvmStatic
        fun make(table: String, fieldJoin: String, fieldReference: String, alias: String, operator: JoinOperator) =
            Join(table, alias, fieldJoin, fieldReference, operator)

        /**
         * Creates a nested join structure from a dot notation path.
         * Example: "user.brands.settings" creates: root -> user -> brands -> settings
         * 
         * @param path dot-separated path (e.g., "user.brands.settings")
         * @param operator the join operator to use for all joins in the path
         * @return the root join with nested deepJoins configured
         */
        @JvmStatic
        @JvmOverloads
        fun makeDeep(path: String, operator: JoinOperator = JoinOperator.INNER): Join {
            val parts = path.split(".")
            if (parts.isEmpty()) {
                throw IllegalArgumentException("Path cannot be empty")
            }
            
            // Create the root join
            val rootJoin = Join(parts[0], joinOperator = operator)
            
            // Build nested structure
            var currentJoin = rootJoin
            for (i in 1 until parts.size) {
                val nestedJoin = Join(parts[i], joinOperator = operator)
                currentJoin.deepJoins.add(nestedJoin)
                currentJoin = nestedJoin
            }
            
            return rootJoin
        }

        /**
         * Creates a nested join structure from a dot notation path with a specific alias for the final join.
         * 
         * @param path dot-separated path (e.g., "user.brands.settings")
         * @param alias the alias for the final join in the path
         * @param operator the join operator to use for all joins in the path
         * @return the root join with nested deepJoins configured
         */
        @JvmStatic
        @JvmOverloads
        fun makeDeep(path: String, alias: String, operator: JoinOperator = JoinOperator.INNER): Join {
            val rootJoin = makeDeep(path, operator)
            
            // Find the last join in the chain and set its alias
            var currentJoin = rootJoin
            while (currentJoin.deepJoins.isNotEmpty()) {
                currentJoin = currentJoin.deepJoins[0]
            }
            currentJoin.alias = alias
            
            return rootJoin
        }
    }
}
