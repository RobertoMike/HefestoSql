package io.github.robertomike.hefesto.builders

import io.github.robertomike.hefesto.actions.wheres.WhereRaw
import io.github.robertomike.hefesto.constructors.*
import io.github.robertomike.hefesto.enums.JoinOperator
import io.github.robertomike.hefesto.enums.SelectOperator
import io.github.robertomike.hefesto.enums.WhereOperator
import io.github.robertomike.hefesto.models.BaseModel
import io.github.robertomike.hefesto.utils.FluentHibernateResultTransformer
import io.github.robertomike.hefesto.utils.Page
import io.github.robertomike.hefesto.utils.SharedMethods
import io.github.robertomike.hefesto.utils.SubQueryContext
import org.hibernate.QueryException
import org.hibernate.Session
import org.hibernate.query.Query
import java.util.*
import java.util.function.Consumer

class Hefesto<T : BaseModel> : BaseBuilder<T, Session, ConstructWhereImplementation, ConstructJoinImplementation,
        ConstructOrderImplementation, ConstructSelectImplementation, ConstructGroupByImplementation, Hefesto<T>>,
        SharedMethods<Hefesto<T>>{

    override val joinsFetch = ConstructJoinFetch()
    var acronymTable: String = ""
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
        val joinsFetchQuery = if (!isCounting) joinsFetch.construct(this) else ""

        return listOf(
            query, acronymTable,
            joins.construct(this), joinsFetchQuery,
            wheres.construct(params, acronymTable),
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
            wheres.construct(params, acronymTable),
            orders.construct(),
            groupBy.construct()
        ).joinToString(" ")
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
        val subQueryBuilder = Hefesto.make(subQueryModel)
        val context = SubQueryContext(subQueryBuilder)
        configurer.accept(context)
        return whereIn(field, subQueryBuilder) as Hefesto<T>
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
        val subQueryBuilder = Hefesto.make(subQueryModel)
        val context = SubQueryContext(subQueryBuilder)
        configurer.accept(context)
        return whereNotIn(field, subQueryBuilder) as Hefesto<T>
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
        val subQueryBuilder = Hefesto.make(subQueryModel)
        val context = SubQueryContext(subQueryBuilder)
        configurer.accept(context)
        return orWhereIn(field, subQueryBuilder) as Hefesto<T>
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
        val subQueryBuilder = Hefesto.make(subQueryModel)
        val context = SubQueryContext(subQueryBuilder)
        configurer.accept(context)
        return orWhereNotIn(field, subQueryBuilder) as Hefesto<T>
    }

    // Note: orWhereExists/orWhereNotExists are not supported in HQL base builder
    // Use whereRaw() or orWhereRaw() for custom EXISTS queries if needed

    // ================================
    // Aggregate Function Shortcuts
    // ================================

    /**
     * Adds a COUNT aggregate function for the specified field.
     * This is a convenience method that wraps addSelect with SelectOperator.COUNT.
     *
     * Usage:
     * ```java
     * // Count all records
     * var builder = Hefesto.make(User.class).count();
     *
     * // Count specific field
     * var builder = Hefesto.make(User.class).count("id");
     * ```
     *
     * @param field The field name to count (defaults to "*" for COUNT(*))
     * @return this builder for chaining
     */
    @JvmOverloads
    fun count(field: String = "*"): Hefesto<T> {
        return addSelect(field, SelectOperator.COUNT) as Hefesto<T>
    }

    /**
     * Adds a COUNT aggregate function with an alias.
     * Useful for groupBy queries where you need to reference the count in results.
     *
     * Usage:
     * ```java
     * var results = Hefesto.make(UserPet.class)
     *     .addSelect("user.id")
     *     .count("id", "petCount")
     *     .groupBy("user")
     *     .get();
     * ```
     *
     * @param field The field name to count
     * @param alias The alias for the aggregate result
     * @return this builder for chaining
     */
    fun count(field: String, alias: String): Hefesto<T> {
        return addSelect(field, alias, SelectOperator.COUNT) as Hefesto<T>
    }

    /**
     * Adds a SUM aggregate function for the specified field.
     *
     * Usage:
     * ```java
     * var builder = Hefesto.make(Order.class).sum("amount");
     * ```
     *
     * @param field The field name to sum
     * @return this builder for chaining
     */
    fun sum(field: String): Hefesto<T> {
        return addSelect(field, SelectOperator.SUM) as Hefesto<T>
    }

    /**
     * Adds a SUM aggregate function with an alias.
     *
     * Usage:
     * ```java
     * var stats = Hefesto.make(Order.class)
     *     .addSelect("userId")
     *     .sum("amount", "totalAmount")
     *     .groupBy("userId")
     *     .get();
     * ```
     *
     * @param field The field name to sum
     * @param alias The alias for the aggregate result
     * @return this builder for chaining
     */
    fun sum(field: String, alias: String): Hefesto<T> {
        return addSelect(field, alias, SelectOperator.SUM) as Hefesto<T>
    }

    /**
     * Adds an AVG (average) aggregate function for the specified field.
     *
     * Usage:
     * ```java
     * var builder = Hefesto.make(User.class).avg("age");
     * ```
     *
     * @param field The field name to average
     * @return this builder for chaining
     */
    fun avg(field: String): Hefesto<T> {
        return addSelect(field, SelectOperator.AVG) as Hefesto<T>
    }

    /**
     * Adds an AVG aggregate function with an alias.
     *
     * Usage:
     * ```java
     * var stats = Hefesto.make(Employee.class)
     *     .addSelect("department")
     *     .avg("salary", "avgSalary")
     *     .groupBy("department")
     *     .get();
     * ```
     *
     * @param field The field name to average
     * @param alias The alias for the aggregate result
     * @return this builder for chaining
     */
    fun avg(field: String, alias: String): Hefesto<T> {
        return addSelect(field, alias, SelectOperator.AVG) as Hefesto<T>
    }

    /**
     * Adds a MIN aggregate function for the specified field.
     *
     * Usage:
     * ```java
     * var builder = Hefesto.make(Product.class).min("price");
     * ```
     *
     * @param field The field name to find minimum
     * @return this builder for chaining
     */
    fun min(field: String): Hefesto<T> {
        return addSelect(field, SelectOperator.MIN) as Hefesto<T>
    }

    /**
     * Adds a MIN aggregate function with an alias.
     *
     * Usage:
     * ```java
     * var stats = Hefesto.make(Product.class)
     *     .addSelect("category")
     *     .min("price", "minPrice")
     *     .groupBy("category")
     *     .get();
     * ```
     *
     * @param field The field name to find minimum
     * @param alias The alias for the aggregate result
     * @return this builder for chaining
     */
    fun min(field: String, alias: String): Hefesto<T> {
        return addSelect(field, alias, SelectOperator.MIN) as Hefesto<T>
    }

    /**
     * Adds a MAX aggregate function for the specified field.
     *
     * Usage:
     * ```java
     * var builder = Hefesto.make(Product.class).max("price");
     * ```
     *
     * @param field The field name to find maximum
     * @return this builder for chaining
     */
    fun max(field: String): Hefesto<T> {
        return addSelect(field, SelectOperator.MAX) as Hefesto<T>
    }

    /**
     * Adds a MAX aggregate function with an alias.
     *
     * Usage:
     * ```java
     * var stats = Hefesto.make(Product.class)
     *     .addSelect("category")
     *     .max("price", "maxPrice")
     *     .groupBy("category")
     *     .get();
     * ```
     *
     * @param field The field name to find maximum
     * @param alias The alias for the aggregate result
     * @return this builder for chaining
     */
    fun max(field: String, alias: String): Hefesto<T> {
        return addSelect(field, alias, SelectOperator.MAX) as Hefesto<T>
    }


}
