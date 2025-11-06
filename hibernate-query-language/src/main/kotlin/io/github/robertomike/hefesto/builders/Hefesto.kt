package io.github.robertomike.hefesto.builders

import io.github.robertomike.hefesto.actions.wheres.WhereRaw
import io.github.robertomike.hefesto.constructors.*
import io.github.robertomike.hefesto.enums.JoinOperator
import io.github.robertomike.hefesto.enums.WhereOperator
import io.github.robertomike.hefesto.models.BaseModel
import io.github.robertomike.hefesto.utils.FluentHibernateResultTransformer
import io.github.robertomike.hefesto.utils.Page
import org.hibernate.QueryException
import org.hibernate.Session
import org.hibernate.query.Query
import java.util.*

class Hefesto<T : BaseModel> : BaseBuilder<T, Session, ConstructWhereImplementation, ConstructJoinImplementation,
        ConstructOrderImplementation, ConstructSelectImplementation, ConstructGroupByImplementation, Hefesto<T>> {

    private val _joinsFetch = ConstructJoinFetch()
    var acronymTable: String = ""
        private set
    private var isCounting = false
    private var originalModel: Class<out BaseModel>? = null

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
        this.limit = 1
        return Optional.ofNullable(this.createQuery<T>().singleResultOrNull)
    }

    /**
     * Creates a query for the given criteria.
     *
     * @return The created query.
     */
    fun <R> createQuery(): Query<R> {
        return applyTransformer(createBaseQuery())
    }

    fun <R> createBaseQuery(): Query<R> {
        val params = mutableMapOf<String, Any?>()

        @Suppress("UNCHECKED_CAST")
        val query = getSessionInstance().createQuery(getQuery(params)) as Query<R>

        params.forEach { (key, value) -> query.setParameter(key, value) }

        if (isCounting) {
            return query
        }

        if (limit != null) {
            query.maxResults = limit!!
        }
        if (offset != null) {
            query.firstResult = offset!!
        }

        return query
    }

    @Suppress("DEPRECATION")
    private fun <R> applyTransformer(query: Query<R>): Query<R> {
        if (isBasicClass(model)) {
            return query
        }
        return query.setTupleTransformer(FluentHibernateResultTransformer(model))
    }

    private fun isBasicClass(clazz: Class<*>): Boolean {
        return clazz == String::class.java || clazz == Boolean::class.javaObjectType || clazz == Char::class.javaObjectType ||
                clazz == Byte::class.javaObjectType || clazz == Short::class.javaObjectType || clazz == Int::class.javaObjectType ||
                clazz == Long::class.javaObjectType || clazz == Float::class.javaObjectType || clazz == Double::class.javaObjectType
    }

    @Suppress("DEPRECATION")
    private fun <R> applyTransformer(query: Query<R>, result: Class<R>): Query<R> {
        if (isBasicClass(result)) {
            return query
        }
        return query.setTupleTransformer(FluentHibernateResultTransformer(result))
    }

    /**
     * Retrieves a list of objects.
     *
     * @return a list of objects
     */
    override fun get(): List<T> {
        return this.createQuery<T>().list()
    }

    /**
     * Retrieves a page of results from the database based on the specified limit and offset.
     *
     * @param limit  the maximum number of results to retrieve
     * @param offset the starting position of the results
     * @return a Page object containing the retrieved results, the offset used, and the total number of results
     */
    override fun page(limit: Int, offset: Long): Page<T> {
        this.offset = offset.toInt()
        this.limit = limit

        val total = countResults()

        return Page(this.createQuery<T>().list(), offset, total)
    }

    /**
     * Counts the number of results based on the given criteria.
     *
     * @return the count of results as a Long value
     */
    override fun countResults(): Long {
        isCounting = true

        val total = this.createBaseQuery<Long>().singleResult

        isCounting = false

        return total
    }

    /**
     * Find the first result of the specified result class.
     *
     * @param resultClass the class of the result to be returned
     * @return the first result of the specified class, or null if no result is found
     */
    fun <R> findFirstFor(resultClass: Class<R>): R {
        if (selects.isEmpty()) {
            throw QueryException("You need put at least one select")
        }

        this.limit = 1
        return applyTransformer(createBaseQuery(), resultClass).singleResult
    }

    /**
     * Finds and retrieves a list of objects of the specified resultClass.
     *
     * @param resultClass the class of the objects to be retrieved
     * @return a list of objects of the specified resultClass
     */
    fun <R> findFor(resultClass: Class<R>): List<R> {
        return applyTransformer(createBaseQuery(), resultClass).resultList
    }

    fun getQuery(params: MutableMap<String, Any?>): String {
        var query = if (isCounting) "select count($acronymTable)" else selects.construct(this)
        query += " from $table"
        val joinsFetchQuery = if (!isCounting) _joinsFetch.construct(this) else ""

        return listOf(
            query, acronymTable,
            joins.construct(this), joinsFetchQuery,
            wheres.construct(params),
            orders.construct(),
            groupBy.construct()
        ).joinToString(" ")
    }

    fun getSubQuery(params: MutableMap<String, Any?>): String {
        var query = selects.constructSubQuery(this)
        query += " from $table"

        return listOf(
            query, acronymTable,
            joins.construct(this),
            wheres.construct(params),
            orders.construct(),
            groupBy.construct()
        ).joinToString(" ")
    }


}
