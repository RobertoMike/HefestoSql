package io.github.robertomike.hefesto.builders

import io.github.robertomike.hefesto.actions.Join
import io.github.robertomike.hefesto.actions.JoinFetch
import io.github.robertomike.hefesto.actions.wheres.WhereCustom
import io.github.robertomike.hefesto.constructors.*
import io.github.robertomike.hefesto.enums.JoinOperator
import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.enums.WhereOperator
import io.github.robertomike.hefesto.models.BaseModel
import io.github.robertomike.hefesto.utils.Page
import io.github.robertomike.hefesto.utils.SharedMethods
import org.hibernate.QueryException
import org.hibernate.Session
import org.hibernate.query.Query
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import jakarta.persistence.criteria.Subquery
import java.util.*

class Hefesto<T : BaseModel>(model: Class<T>) :
    BaseBuilder<T, Session, ConstructWhereImplementation, ConstructJoinImplementation<T>, ConstructOrderImplementation, ConstructSelectImplementation<T>, ConstructGroupByImplementation, Hefesto<T>>(model),
    SharedMethods<Hefesto<T>> {

    override val joinsFetch = ConstructJoinFetch()
    private var originalModel: Class<*>? = null
    private var customResultSubQuery: Class<*>? = null

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
    
    fun getJoins(): ConstructJoinImplementation<T> {
        return joins
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
        whereCustom { cb, _, root, _, parentRoot ->
            val path1 = io.github.robertomike.hefesto.utils.HibernateUtils.getFieldFrom<Any>(root, field1)
            val path2 = if (parentRoot != null) io.github.robertomike.hefesto.utils.HibernateUtils.getFieldFrom<Any>(parentRoot, field2)
                       else io.github.robertomike.hefesto.utils.HibernateUtils.getFieldFrom<Any>(root, field2)
            cb.equal(path1, path2)
        }
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
        whereCustom { cb, _, root, _, _ ->
            val path1 = io.github.robertomike.hefesto.utils.HibernateUtils.getFieldFrom<Comparable<Any>>(root, field1)
            val path2 = io.github.robertomike.hefesto.utils.HibernateUtils.getFieldFrom<Comparable<Any>>(root, field2)
            
            when (operator) {
                Operator.EQUAL -> cb.equal(path1, path2)
                Operator.DIFF -> cb.notEqual(path1, path2)
                Operator.GREATER -> cb.greaterThan(path1, path2)
                Operator.GREATER_OR_EQUAL -> cb.greaterThanOrEqualTo(path1, path2)
                Operator.LESS -> cb.lessThan(path1, path2)
                Operator.LESS_OR_EQUAL -> cb.lessThanOrEqualTo(path1, path2)
                else -> throw io.github.robertomike.hefesto.exceptions.UnsupportedOperationException("Operator $operator is not supported for field-to-field comparison")
            }
        }
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
        return Optional.ofNullable(createQuery().singleResultOrNull)
    }

    /**
     * Creates a query for the given criteria.
     *
     * @return The created query.
     */
    @Suppress("UNCHECKED_CAST")
    protected fun createQuery(): Query<T> {
        val currentSession = getSessionInstance()
        val cb = currentSession.criteriaBuilder
        val cr = cb.createQuery(model)
        val root = getRoot(cr)

        joinsFetch.construct(root)

        joins.construct(root)
        selects.setJoins(joins.joins)
        if (originalModel == null) {
            selects.construct(root as Root<T>, cr, cb)
        } else {
            selects.multiSelect(root, cr, cb)
        }
        wheres.setJoins(joins.joins).construct(cb, cr, root)
        orders.setJoins(joins.joins).construct(cb, cr, root)
        groupBy.construct(cr, root)

        val query = currentSession.createQuery(cr)

        if (limit != null) {
            query.maxResults = limit!!
        }
        if (offset != null) {
            query.firstResult = offset!!
        }

        return query
    }

    /**
     * Retrieves the root entity for the given CriteriaQuery.
     *
     * @param cr the CriteriaQuery object to retrieve the root from
     * @return the root entity for the given CriteriaQuery
     */
    private fun getRoot(cr: CriteriaQuery<T>): Root<*> {
        return if (originalModel == null) {
            cr.from(model)
        } else {
            cr.from(originalModel)
        }
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
        val sub = if (customResultSubQuery != null) {
            cr.subquery(customResultSubQuery)
        } else {
            cr.subquery(model)
        }
        val root = sub.from(model)

        joins.construct(root)
        val allJoins = HashMap(parentJoins)
        allJoins.putAll(joins.joins)
        wheres.setJoins(allJoins).constructSubQuery(sub, cb, root, parentRoot)
        selects.setJoins(allJoins).constructSubQuery(root, sub)
        groupBy.construct(cr, root)

        return sub
    }

    fun setCustomResultForSubQuery(customResultSubQuery: Class<*>): Hefesto<T> {
        this.customResultSubQuery = customResultSubQuery
        return this
    }

    fun hasCustomResultForSubQuery(): Boolean {
        return customResultSubQuery != null
    }

    /**
     * Retrieves a list of objects.
     *
     * @return a list of objects
     */
    override fun get(): List<T> {
        return createQuery().resultList
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

        return Page(createQuery().resultList, offset, total)
    }

    /**
     * Counts the number of results based on the given criteria.
     *
     * @return the count of results as a Long value
     */
    override fun countResults(): Long {
        val currentSession = getSessionInstance()
        val cb = currentSession.criteriaBuilder
        val cr = cb.createQuery(Long::class.java)
        val root = cr.from(model)

        cr.select(cb.count(root))
        joins.construct(root)
        wheres.setJoins(joins.joins).construct(cb, cr, root)
        groupBy.construct(cr, root)

        return currentSession.createQuery(cr).singleResult
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

        val cr = commonConstructForCustomResult(resultClass)

        val currentSession = getSessionInstance()
        val query = currentSession.createQuery(cr)
        query.maxResults = 1

        return query.singleResult
    }

    /**
     * Finds and retrieves a list of objects of the specified resultClass.
     *
     * @param resultClass the class of the objects to be retrieved
     * @return a list of objects of the specified resultClass
     */
    fun <R> findFor(resultClass: Class<R>): List<R> {
        if (selects.isEmpty()) {
            throw QueryException("You need put at least one select")
        }

        val cr = commonConstructForCustomResult(resultClass)

        val currentSession = getSessionInstance()
        return currentSession.createQuery(cr).resultList
    }

    /**
     * Generates a common criteria query for custom result.
     *
     * @param resultClass the class of the result
     * @return the generated criteria query
     */
    private fun <R> commonConstructForCustomResult(resultClass: Class<R>): CriteriaQuery<R> {
        val currentSession = getSessionInstance()
        val cb = currentSession.criteriaBuilder
        val cr = cb.createQuery(resultClass)
        val root = cr.from(model)

        joins.construct(root)
        selects.setJoins(joins.joins)
            .multiSelect(root, cr, cb)
        wheres.setJoins(joins.joins)
            .construct(cb, cr, root)
        orders.setJoins(joins.joins)
            .construct(cb, cr, root)
        groupBy.construct(cr, root)

        return cr
    }
}
