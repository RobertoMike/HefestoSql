package io.github.robertomike.hefesto.hql.builders

import io.github.robertomike.hefesto.hql.actions.wheres.WhereRaw
import io.github.robertomike.hefesto.builders.BaseBuilder
import io.github.robertomike.hefesto.enums.JoinOperator
import io.github.robertomike.hefesto.enums.WhereOperator
import io.github.robertomike.hefesto.hql.constructors.ConstructGroupByImplementation
import io.github.robertomike.hefesto.hql.constructors.ConstructJoinFetch
import io.github.robertomike.hefesto.hql.constructors.ConstructJoinImplementation
import io.github.robertomike.hefesto.hql.constructors.ConstructOrderImplementation
import io.github.robertomike.hefesto.hql.constructors.ConstructSelectImplementation
import io.github.robertomike.hefesto.hql.constructors.ConstructWhereImplementation
import io.github.robertomike.hefesto.models.BaseModel
import io.github.robertomike.hefesto.utils.Page
import io.github.robertomike.hefesto.utils.SharedMethods
import io.github.robertomike.hefesto.hql.utils.SubQueryContext
import org.hibernate.Session
import org.hibernate.query.Query
import java.util.*
import java.util.function.Consumer

class Hefesto<T : BaseModel> : BaseBuilder<T, Session, ConstructWhereImplementation, ConstructJoinImplementation,
        ConstructOrderImplementation, ConstructSelectImplementation, ConstructGroupByImplementation, Hefesto<T>>,
        SharedMethods<Hefesto<T>> {

    override val joinsFetch = ConstructJoinFetch()
    var acronymTable: String = ""
    private var originalModel: Class<out BaseModel>? = null

    // Lazy initialization of executor - created only when query execution is needed
    private val executor: HefestoExecutor<T> by lazy {
        HefestoExecutor(model, table!!, acronymTable)
    }

    constructor(model: Class<T>) : super(model) {
        setup()
    }

    constructor(modelEntity: Class<out BaseModel>, dto: Class<T>) : super(dto) {
        originalModel = modelEntity
        setup()
    }

    private fun setup() {
        orders = ConstructOrderImplementation()
        wheres = ConstructWhereImplementation()
        joins = ConstructJoinImplementation()
        selects = ConstructSelectImplementation()
        groupBy = ConstructGroupByImplementation()
        table = (originalModel ?: model).simpleName
        acronymTable = table!!.lowercase()
    }

    /**
     * Create a new instance of the Hefesto class with the given model.
     *
     * @param model the model class to be used
     * @return a new instance of Hefesto with the given model
     */
    companion object {
        @JvmStatic
        fun <T : BaseModel> make(model: Class<T>): Hefesto<T> {
            return Hefesto(model)
        }
    }

    /**
     * Unsupported method - use join with Join object instead
     *
     * @param table     the table to join
     * @param joinField the field to join on
     * @param operator  the join operator
     * @return return the current instance
     */
    @Suppress("UNUSED_PARAMETER")
    fun joinUnsupported(table: String, joinField: String, operator: JoinOperator): Hefesto<T> {
        throw UnsupportedOperationException("This methods are not supported")
    }

    /**
     * This method is not supported in HQL version
     */
    override fun join(table: String, joinField: String, operator: JoinOperator): Hefesto<T> {
        throw UnsupportedOperationException("This method is not supported in HQL version")
    }

    /**
     * This method adds a where that allow you to pass lambda and return a Predicate
     */
    fun whereRaw(custom: WhereRaw): Hefesto<T> {
        wheres.add(custom)
        return this
    }

    /**
     * This method adds a where that allow you to pass lambda and return a Predicate
     */
    fun whereRaw(raw: String): Hefesto<T> {
        wheres.add(WhereRaw(raw))
        return this
    }

    /**
     * This method adds a where with or that allow you to pass lambda and return a Predicate
     */
    fun orWhereRaw(raw: String): Hefesto<T> {
        wheres.add(WhereRaw(raw, WhereOperator.OR))
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
        return executor.findFirst(getSessionInstance(), selects, wheres, joins, joinsFetch, orders, groupBy, this)
    }

    /**
     * Creates a query for the given criteria.
     *
     * @return The created query.
     */
    fun <R> createQuery(): Query<R> {
        return executor.createQuery(getSessionInstance(), selects, wheres, joins, joinsFetch, orders, groupBy, limit, offset, this)
    }

    fun <R> createBaseQuery(): Query<R> {
        return executor.createBaseQuery(getSessionInstance(), selects, wheres, joins, joinsFetch, orders, groupBy, limit, offset, false, this)
    }

    /**
     * Retrieves a list of objects.
     *
     * @return a list of objects
     */
    override fun get(): List<T> {
        return executor.get(getSessionInstance(), selects, wheres, joins, joinsFetch, orders, groupBy, limit, offset, this)
    }

    /**
     * Retrieves a page of results from the database based on the specified limit and offset.
     *
     * @param limit  the maximum number of results to retrieve
     * @param offset the starting position of the results
     * @return a Page object containing the retrieved results, the offset used, and the total number of results
     */
    override fun page(limit: Int, offset: Long): Page<T> {
        return executor.page(getSessionInstance(), selects, wheres, joins, joinsFetch, orders, groupBy, limit, offset, this)
    }

    /**
     * Counts the number of results based on the given criteria.
     *
     * @return the count of results as a Long value
     */
    override fun countResults(): Long {
        return executor.countResults(getSessionInstance(), wheres, joins, groupBy, this)
    }

    /**
     * Find the first result of the specified result class.
     *
     * @param resultClass the class of the result to be returned
     * @return the first result of the specified class, or null if no result is found
     */
    fun <R> findFirstFor(resultClass: Class<R>): R {
        return executor.findFirstFor(getSessionInstance(), resultClass, selects, wheres, joins, joinsFetch, orders, groupBy, this)
    }

    /**
     * Finds and retrieves a list of objects of the specified resultClass.
     *
     * @param resultClass the class of the objects to be retrieved
     * @return a list of objects of the specified resultClass
     */
    fun <R> findFor(resultClass: Class<R>): List<R> {
        return executor.findFor(getSessionInstance(), resultClass, selects, wheres, joins, joinsFetch, orders, groupBy, limit, offset, this)
    }

    fun getQuery(params: MutableMap<String, Any?>): String {
        return executor.getQuery(selects, wheres, joins, joinsFetch, orders, groupBy, params, false, this)
    }

    fun getSubQuery(params: MutableMap<String, Any?>): String {
        return executor.getSubQuery(selects, wheres, joins, orders, groupBy, params, this)
    }

    // ================================
    // Lambda-based Subquery Methods
    // ================================

    /**
     * Adds a WHERE IN clause using a lambda-based subquery.
     *
     * Usage:
     * ```java
     * var users = Hefesto.make(User.class)
     *     .whereIn("id", UserPet.class, subQuery -> {
     *         subQuery.addSelect("user.id");
     *         subQuery.where("petType", "DOG");
     *     })
     *     .get();
     * ```
     *
     * @param field The field to check against the subquery results
     * @param subQueryModel The entity class for the subquery
     * @param configurer Lambda to configure the subquery
     * @return this builder for chaining
     */
    fun <S : BaseModel> whereIn(field: String, subQueryModel: Class<S>, configurer: Consumer<SubQueryContext<S>>): Hefesto<T> {
        val subQuery = make(subQueryModel)
        val context = SubQueryContext(subQuery)
        configurer.accept(context)
        return whereIn(field, subQuery)
    }

    /**
     * Adds a WHERE NOT IN clause using a lambda-based subquery.
     *
     * Usage:
     * ```java
     * var users = Hefesto.make(User.class)
     *     .whereNotIn("id", UserPet.class, subQuery -> {
     *         subQuery.addSelect("user.id");
     *         subQuery.where("petType", "CAT");
     *     })
     *     .get();
     * ```
     *
     * @param field The field to check against the subquery results
     * @param subQueryModel The entity class for the subquery
     * @param configurer Lambda to configure the subquery
     * @return this builder for chaining
     */
    fun <S : BaseModel> whereNotIn(field: String, subQueryModel: Class<S>, configurer: Consumer<SubQueryContext<S>>): Hefesto<T> {
        val subQuery = make(subQueryModel)
        val context = SubQueryContext(subQuery)
        configurer.accept(context)
        return whereNotIn(field, subQuery)
    }

    // Note: whereExists/whereNotExists are not supported in HQL base builder
    // Use whereRaw() for custom EXISTS queries if needed

    /**
     * Adds an OR WHERE IN clause using a lambda-based subquery.
     *
     * @param field The field to check against the subquery results
     * @param subQueryModel The entity class for the subquery
     * @param configurer Lambda to configure the subquery
     * @return this builder for chaining
     */
    fun <S : BaseModel> orWhereIn(field: String, subQueryModel: Class<S>, configurer: Consumer<SubQueryContext<S>>): Hefesto<T> {
        val subQuery = make(subQueryModel)
        val context = SubQueryContext(subQuery)
        configurer.accept(context)
        return orWhereIn(field, subQuery)
    }

    /**
     * Adds an OR WHERE NOT IN clause using a lambda-based subquery.
     *
     * @param field The field to check against the subquery results
     * @param subQueryModel The entity class for the subquery
     * @param configurer Lambda to configure the subquery
     * @return this builder for chaining
     */
    fun <S : BaseModel> orWhereNotIn(field: String, subQueryModel: Class<S>, configurer: Consumer<SubQueryContext<S>>): Hefesto<T> {
        val subQuery = make(subQueryModel)
        val context = SubQueryContext(subQuery)
        configurer.accept(context)
        return orWhereNotIn(field, subQuery)
    }

    // Note: orWhereExists/orWhereNotExists are not supported in HQL base builder
    // Use whereRaw() or orWhereRaw() for custom EXISTS queries if needed

}
