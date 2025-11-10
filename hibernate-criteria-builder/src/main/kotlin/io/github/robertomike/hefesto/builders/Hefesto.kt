package io.github.robertomike.hefesto.builders

import io.github.robertomike.hefesto.actions.Join
import io.github.robertomike.hefesto.actions.JoinFetch
import io.github.robertomike.hefesto.actions.wheres.WhereCustom
import io.github.robertomike.hefesto.actions.wheres.WhereField
import io.github.robertomike.hefesto.constructors.*
import io.github.robertomike.hefesto.enums.JoinOperator
import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.enums.WhereOperator
import io.github.robertomike.hefesto.models.BaseModel
import io.github.robertomike.hefesto.utils.Page
import io.github.robertomike.hefesto.utils.SharedMethods
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import jakarta.persistence.criteria.Subquery
import java.util.*

class Hefesto<T : BaseModel>(model: Class<T>) :
    BaseBuilder<T, org.hibernate.Session, ConstructWhereImplementation, ConstructJoinImplementation<T>, ConstructOrderImplementation, ConstructSelectImplementation<T>, ConstructGroupByImplementation, Hefesto<T>>(model),
    SharedMethods<Hefesto<T>> {

    override val joinsFetch = ConstructJoinFetch()
    private var originalModel: Class<*>? = null
    private var customResultSubQuery: Class<*>? = null
    
    // Internal executor - separates building from execution
    private val executor: HefestoExecutor<T> by lazy {
        HefestoExecutor(model, originalModel, customResultSubQuery)
    }

    init {
        orders = ConstructOrderImplementation()
        wheres = ConstructWhereImplementation()
        joins = ConstructJoinImplementation()
        selects = ConstructSelectImplementation()
        groupBy = ConstructGroupByImplementation()
    }

    constructor(modelEntity: Class<out BaseModel>, dto: Class<T>) : this(dto) {
        this.originalModel = modelEntity
    }

    companion object {
        /**
         * Create a new instance of the Hefesto class with the given model.
         *
         * @param model the model class to be used
         * @return a new instance of Hefesto with the given model
         */
        @JvmStatic
        fun <T : BaseModel> make(model: Class<T>): Hefesto<T> {
            return Hefesto(model)
        }

        /**
         * Create a new instance of the Hefesto class with the given model.
         *
         * @param model the model class to be used
         * @param dto the model class that will be the result
         * @return a new instance of Hefesto with the given model
         */
        @JvmStatic
        fun <T : BaseModel> make(model: Class<out BaseModel>, dto: Class<T>): Hefesto<T> {
            return Hefesto(model, dto)
        }
    }

    /**
     * Adds a join to load in the query.
     *
     * @param field the field to join on
     * @param alias this alias is not for the query is only when you need to use the join in a where condition or select
     * @return the updated Hefesto object
     */
    override fun join(field: String, alias: String): Hefesto<T> {
        joins.add(Join.make(field, alias))
        return this
    }

    /**
     * Adds a join with default alias (same as field name).
     *
     * @param field the field to join on
     * @return the updated Hefesto object
     */
    override fun join(field: String): Hefesto<T> {
        joins.add(Join.make(field, field))
        return this
    }

    /**
     * Adds a join with specific join type.
     *
     * @param field the field to join on
     * @param joinType the type of join (LEFT, RIGHT, INNER, etc.)
     * @return the updated Hefesto object
     */
    override fun join(field: String, joinType: JoinOperator): Hefesto<T> {
        joins.add(Join.make(field, field, joinType))
        return this
    }

    /**
     * Creates a deep join using dot notation path.
     * Example: "user.brands.settings" creates nested joins: root -> user -> brands -> settings
     *
     * @param path dot-separated relationship path
     * @param operator the join operator to use for all joins in the path
     * @return the updated Hefesto object
     */
    @JvmOverloads
    fun joinDeep(path: String, operator: JoinOperator = JoinOperator.INNER): Hefesto<T> {
        joins.add(Join.makeDeep(path, operator))
        return this
    }

    /**
     * Creates a deep join using dot notation path with an alias for the final join.
     * Example: "user.brands.settings" with alias "userSettings"
     *
     * @param path dot-separated relationship path
     * @param alias the alias for the final join in the path
     * @param operator the join operator to use for all joins in the path
     * @return the updated Hefesto object
     */
    @JvmOverloads
    fun joinDeep(path: String, alias: String, operator: JoinOperator = JoinOperator.INNER): Hefesto<T> {
        joins.add(Join.makeDeep(path, alias, operator))
        return this
    }

    /**
     * Adds a join with inline conditions using a builder lambda.
     * This allows you to configure the join with an alias and WHERE conditions.
     * 
     * Example:
     * ```java
     * Hefesto.make(User.class)
     *     .join("pets", join -> {
     *         join.alias("Pet");
     *         join.where("name", "lola");
     *     })
     *     .get();
     * ```
     *
     * @param field the field to join on
     * @param configurator lambda to configure the join (alias, conditions, etc.)
     * @return the updated Hefesto object
     */
    fun join(field: String, configurator: java.util.function.Consumer<JoinBuilder>): Hefesto<T> {
        val builder = JoinBuilder(field)
        configurator.accept(builder)
        joins.add(builder.build())
        return this
    }

    /**
     * Adds a join with specific join type and inline conditions using a builder lambda.
     *
     * @param field the field to join on
     * @param joinType the type of join (LEFT, RIGHT, INNER, etc.)
     * @param configurator lambda to configure the join (alias, conditions, etc.)
     * @return the updated Hefesto object
     */
    fun join(field: String, joinType: JoinOperator, configurator: java.util.function.Consumer<JoinBuilder>): Hefesto<T> {
        val builder = JoinBuilder(field, joinType)
        configurator.accept(builder)
        joins.add(builder.build())
        return this
    }

    /**
     * Adds fetch joins for eager loading relationships.
     *
     * @param fields the fields to fetch
     * @return the updated Hefesto object
     */
    override fun with(vararg fields: String): Hefesto<T> {
        for (field in fields) {
            joinsFetch.add(JoinFetch.make(field))
        }
        return this
    }

    /**
     * Adds a fetch join with specific join type.
     *
     * @param field the field to fetch
     * @param joinType the type of join
     * @return the updated Hefesto object
     */
    override fun with(field: String, joinType: jakarta.persistence.criteria.JoinType): Hefesto<T> {
        joinsFetch.add(JoinFetch.make(field, joinType))
        return this
    }

    /**
     * Adds a pre-created JoinFetch.
     *
     * @param joinFetch the JoinFetch to add
     * @return the updated Hefesto object
     */
    fun with(joinFetch: JoinFetch): Hefesto<T> {
        joinsFetch.add(joinFetch)
        return this
    }

    fun getSelectsSize(): Int {
        return selects.size
    }

    /**
     * This method adds a where that allow you to pass lambda and return a Predicate
     */
    fun whereCustom(custom: WhereCustom.Custom): Hefesto<T> {
        wheres.add(WhereCustom(custom))
        return this
    }

    /**
     * This method adds a where with or that allow you to pass lambda and return a Predicate
     */
    fun orWhereCustom(custom: WhereCustom.Custom): Hefesto<T> {
        wheres.add(WhereCustom(custom, WhereOperator.OR))
        return this
    }

    /**
     * Adds a WHERE clause comparing two fields.
     * Useful for subqueries where you need to compare parent field to subquery field.
     *
     * @param field1 the first field name
     * @param field2 the second field name
     * @return the updated Hefesto object
     */
    override fun whereField(field1: String, field2: String): Hefesto<T> {
        wheres.add(WhereField(field1, field2))
        return this
    }

    /**
     * Adds a WHERE clause comparing two fields with a specific operator.
     *
     * @param field1 the first field name
     * @param operator the comparison operator
     * @param field2 the second field name
     * @return the updated Hefesto object
     */
    override fun whereField(field1: String, operator: Operator, field2: String): Hefesto<T> {
        wheres.add(WhereField(field1, operator, field2))
        return this
    }

    /**
     * Returns an Optional containing the first element of the result set,
     * or an empty Optional if the result set is empty.
     *
     * @return an Optional containing the first element of the result set,
     * or an empty Optional if the result set is empty.
     */
    override fun findFirst(): Optional<T> {
        return executor.findFirst(getSessionInstance(), selects, wheres, joins, joinsFetch, orders, groupBy)
    }

    /**
     * Retrieves a list of objects.
     *
     * @return a list of objects
     */
    override fun get(): List<T> {
        return executor.get(getSessionInstance(), selects, wheres, joins, joinsFetch, orders, groupBy, limit, offset)
    }

    /**
     * Retrieves a page of results from the database based on the specified limit and offset.
     *
     * @param limit  the maximum number of results to retrieve
     * @param offset the starting position of the results
     * @return a Page object containing the retrieved results, the offset used, and the total number of results
     */
    override fun page(limit: Int, offset: Long): Page<T> {
        return executor.page(getSessionInstance(), selects, wheres, joins, joinsFetch, orders, groupBy, limit, offset)
    }

    /**
     * Counts the number of results based on the given criteria.
     *
     * @return the count of results as a Long value
     */
    override fun countResults(): Long {
        return executor.countResults(getSessionInstance(), wheres, joins, groupBy)
    }

    /**
     * Generates a sub-query for the given criteria query, root, criteria builder, and joins.
     *
     * @param cr          the criteria query
     * @param parentRoot  the parent root
     * @param cb          the criteria builder
     * @param parentJoins the joins
     * @return the generated subquery
     */
    fun getSubQuery(
        cr: CriteriaQuery<*>,
        parentRoot: Root<*>,
        cb: CriteriaBuilder,
        parentJoins: Map<String, jakarta.persistence.criteria.Join<*, *>>
    ): Subquery<*> {
        return executor.createSubQuery(cr, parentRoot, cb, parentJoins, selects, wheres, joins, groupBy)
    }

    fun setCustomResultForSubQuery(customResultSubQuery: Class<*>): Hefesto<T> {
        this.customResultSubQuery = customResultSubQuery
        return this
    }

    fun hasCustomResultForSubQuery(): Boolean {
        return customResultSubQuery != null
    }

    /**
     * Find the first result of the specified result class.
     *
     * @param resultClass the class of the result to be returned
     * @return the first result of the specified class, or null if no result is found
     */
    fun <R> findFirstFor(resultClass: Class<R>): R {
        return executor.findFirstFor(getSessionInstance(), resultClass, selects, wheres, joins, orders, groupBy, offset)
    }

    /**
     * Finds and retrieves a list of objects of the specified resultClass.
     *
     * @param resultClass the class of the objects to be retrieved
     * @return a list of objects of the specified resultClass
     */
    fun <R> findFor(resultClass: Class<R>): List<R> {
        return executor.findFor(getSessionInstance(), resultClass, selects, wheres, joins, orders, groupBy, limit, offset)
    }

    // ========== HELPER METHODS ==========
    
    /**
     * Infers and sets the result type for a subquery based on the field type.
     * If the field exists in the model, uses its type. Otherwise, defaults to Long.
     *
     * @param field the field name to infer type from
     * @param subQuery the subquery to configure
     */
    private fun <S : BaseModel> inferSubQueryResultType(field: String, subQuery: Hefesto<S>) {
        if (!subQuery.hasCustomResultForSubQuery() && subQuery.getSelectsSize() > 0) {
            try {
                val fieldObj = model.getDeclaredField(field)
                subQuery.setCustomResultForSubQuery(fieldObj.type)
            } catch (_: NoSuchFieldException) {
                // Field might be from a join or deep path, default to Long
                subQuery.setCustomResultForSubQuery(Long::class.java)
            }
        }
    }

    // ========== LAMBDA-BASED SUBQUERY METHODS ==========

    /**
     * Adds a WHERE IN clause with a lambda-configured subquery.
     * The subquery result type is automatically inferred from the field type.
     *
     * Example:
     * ```
     * // Java
     * whereIn("id", UserPet.class, subQuery -> {
     *     subQuery.addSelect("user.id");
     *     subQuery.where("active", true);
     * })
     *
     * // Kotlin
     * whereIn("id", UserPet::class.java) {
     *     addSelect("user.id")
     *     where("active", true)
     * }
     * ```
     *
     * @param field the field to apply the WHERE IN clause on
     * @param subQueryModel the entity class for the subquery
     * @param block the lambda to configure the subquery
     * @return the updated Hefesto object
     */
    fun <S : BaseModel> whereIn(
        field: String,
        subQueryModel: Class<S>,
        block: java.util.function.Consumer<io.github.robertomike.hefesto.utils.SubQueryContext<Hefesto<S>>>
    ): Hefesto<T> {
        val subQuery = io.github.robertomike.hefesto.utils.SubQueryConfigurer.configureSubQuery(
            subQueryModel,
            { make(it) },
            block
        )
        inferSubQueryResultType(field, subQuery)
        return whereIn(field, subQuery)
    }

    /**
     * Adds a WHERE NOT IN clause with a lambda-configured subquery.
     * The subquery result type is automatically inferred from the field type.
     *
     * Example:
     * ```
     * // Java
     * whereNotIn("id", UserPet.class, subQuery -> {
     *     subQuery.addSelect("user.id");
     *     subQuery.where("active", false);
     * })
     * ```
     *
     * @param field the field to apply the WHERE NOT IN clause on
     * @param subQueryModel the entity class for the subquery
     * @param block the lambda to configure the subquery
     * @return the updated Hefesto object
     */
    fun <S : BaseModel> whereNotIn(
        field: String,
        subQueryModel: Class<S>,
        block: java.util.function.Consumer<io.github.robertomike.hefesto.utils.SubQueryContext<Hefesto<S>>>
    ): Hefesto<T> {
        val subQuery = io.github.robertomike.hefesto.utils.SubQueryConfigurer.configureSubQuery(
            subQueryModel,
            { make(it) },
            block
        )
        inferSubQueryResultType(field, subQuery)
        return whereNotIn(field, subQuery)
    }

    /**
     * Adds an OR WHERE IN clause with a lambda-configured subquery.
     * The subquery result type is automatically inferred from the field type.
     *
     * Example:
     * ```
     * // Java
     * orWhereIn("id", UserPet.class, subQuery -> {
     *     subQuery.addSelect("user.id");
     *     subQuery.where("status", "PENDING");
     * })
     * ```
     *
     * @param field the field to apply the WHERE IN clause on
     * @param subQueryModel the entity class for the subquery
     * @param block the lambda to configure the subquery
     * @return the updated Hefesto object
     */
    fun <S : BaseModel> orWhereIn(
        field: String,
        subQueryModel: Class<S>,
        block: java.util.function.Consumer<io.github.robertomike.hefesto.utils.SubQueryContext<Hefesto<S>>>
    ): Hefesto<T> {
        val subQuery = io.github.robertomike.hefesto.utils.SubQueryConfigurer.configureSubQuery(
            subQueryModel,
            { make(it) },
            block
        )
        inferSubQueryResultType(field, subQuery)
        return orWhereIn(field, subQuery)
    }

    /**
     * Adds an OR WHERE NOT IN clause with a lambda-configured subquery.
     * The subquery result type is automatically inferred from the field type.
     *
     * Example:
     * ```
     * // Java
     * orWhereNotIn("id", UserPet.class, subQuery -> {
     *     subQuery.addSelect("user.id");
     *     subQuery.where("deleted", true);
     * })
     * ```
     *
     * @param field the field to apply the WHERE NOT IN clause on
     * @param subQueryModel the entity class for the subquery
     * @param block the lambda to configure the subquery
     * @return the updated Hefesto object
     */
    fun <S : BaseModel> orWhereNotIn(
        field: String,
        subQueryModel: Class<S>,
        block: java.util.function.Consumer<io.github.robertomike.hefesto.utils.SubQueryContext<Hefesto<S>>>
    ): Hefesto<T> {
        val subQuery = io.github.robertomike.hefesto.utils.SubQueryConfigurer.configureSubQuery(
            subQueryModel,
            { make(it) },
            block
        )
        inferSubQueryResultType(field, subQuery)
        return orWhereNotIn(field, subQuery)
    }

    /**
     * Adds a WHERE EXISTS clause with a lambda-configured subquery.
     *
     * Example:
     * ```
     * // Java
     * whereExists(UserPet.class, subQuery -> {
     *     subQuery.whereField("user.id", "id");
     *     subQuery.where("active", true);
     * })
     * ```
     *
     * @param subQueryModel the entity class for the subquery
     * @param block the lambda to configure the subquery
     * @return the updated Hefesto object
     */
    fun <S : BaseModel> whereExists(
        subQueryModel: Class<S>,
        block: java.util.function.Consumer<io.github.robertomike.hefesto.utils.SubQueryContext<Hefesto<S>>>
    ): Hefesto<T> {
        val subQuery = io.github.robertomike.hefesto.utils.SubQueryConfigurer.configureSubQuery(
            subQueryModel,
            { make(it) },
            block
        )
        return whereExists(subQuery)
    }

    /**
     * Adds a WHERE NOT EXISTS clause with a lambda-configured subquery.
     *
     * Example:
     * ```
     * // Java
     * whereNotExists(UserPet.class, subQuery -> {
     *     subQuery.whereField("user.id", "id");
     *     subQuery.where("deleted", true);
     * })
     * ```
     *
     * @param subQueryModel the entity class for the subquery
     * @param block the lambda to configure the subquery
     * @return the updated Hefesto object
     */
    fun <S : BaseModel> whereNotExists(
        subQueryModel: Class<S>,
        block: java.util.function.Consumer<io.github.robertomike.hefesto.utils.SubQueryContext<Hefesto<S>>>
    ): Hefesto<T> {
        val subQuery = io.github.robertomike.hefesto.utils.SubQueryConfigurer.configureSubQuery(
            subQueryModel,
            { make(it) },
            block
        )
        return whereNotExists(subQuery)
    }

    /**
     * Adds an OR WHERE EXISTS clause with a lambda-configured subquery.
     *
     * Example:
     * ```
     * // Java
     * orWhereExists(UserPet.class, subQuery -> {
     *     subQuery.whereField("user.id", "id");
     *     subQuery.where("status", "ACTIVE");
     * })
     * ```
     *
     * @param subQueryModel the entity class for the subquery
     * @param block the lambda to configure the subquery
     * @return the updated Hefesto object
     */
    fun <S : BaseModel> orWhereExists(
        subQueryModel: Class<S>,
        block: java.util.function.Consumer<io.github.robertomike.hefesto.utils.SubQueryContext<Hefesto<S>>>
    ): Hefesto<T> {
        val subQuery = io.github.robertomike.hefesto.utils.SubQueryConfigurer.configureSubQuery(
            subQueryModel,
            { make(it) },
            block
        )
        return orWhereExists(subQuery)
    }

    /**
     * Adds an OR WHERE NOT EXISTS clause with a lambda-configured subquery.
     *
     * Example:
     * ```
     * // Java
     * orWhereNotExists(UserPet.class, subQuery -> {
     *     subQuery.whereField("user.id", "id");
     *     subQuery.where("archived", true);
     * })
     * ```
     *
     * @param subQueryModel the entity class for the subquery
     * @param block the lambda to configure the subquery
     * @return the updated Hefesto object
     */
    fun <S : BaseModel> orWhereNotExists(
        subQueryModel: Class<S>,
        block: java.util.function.Consumer<io.github.robertomike.hefesto.utils.SubQueryContext<Hefesto<S>>>
    ): Hefesto<T> {
        val subQuery = io.github.robertomike.hefesto.utils.SubQueryConfigurer.configureSubQuery(
            subQueryModel,
            { make(it) },
            block
        )
        return orWhereNotExists(subQuery)
    }
}
