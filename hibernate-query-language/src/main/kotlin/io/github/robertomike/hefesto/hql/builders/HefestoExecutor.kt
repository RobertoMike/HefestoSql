package io.github.robertomike.hefesto.hql.builders

import io.github.robertomike.hefesto.hql.constructors.ConstructGroupByImplementation
import io.github.robertomike.hefesto.hql.constructors.ConstructJoinFetch
import io.github.robertomike.hefesto.hql.constructors.ConstructJoinImplementation
import io.github.robertomike.hefesto.hql.constructors.ConstructOrderImplementation
import io.github.robertomike.hefesto.hql.constructors.ConstructSelectImplementation
import io.github.robertomike.hefesto.hql.constructors.ConstructWhereImplementation
import io.github.robertomike.hefesto.hql.utils.FluentHibernateResultTransformer
import io.github.robertomike.hefesto.models.BaseModel
import io.github.robertomike.hefesto.utils.Page
import org.hibernate.QueryException
import org.hibernate.Session
import org.hibernate.query.Query
import java.util.*

/**
 * Internal executor class responsible for query construction and execution in HQL.
 * Separates execution concerns from query building logic.
 *
 * This class is marked as internal and should not be used directly by library users.
 * All query execution goes through the Hefesto builder class.
 */
internal class HefestoExecutor<T : BaseModel>(
    private val model: Class<T>,
    private val table: String,
    private val acronymTable: String
) {
    /**
     * Creates a base Query object from the builder components.
     */
    fun <R> createBaseQuery(
        session: Session,
        selects: ConstructSelectImplementation,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        limit: Int?,
        offset: Int?,
        isCounting: Boolean,
        hefesto: Hefesto<T>
    ): Query<R> {
        val params = mutableMapOf<String, Any?>()

        @Suppress("UNCHECKED_CAST")
        val query = session.createQuery(getQuery(
            selects, wheres, joins, joinsFetch, orders, groupBy, 
            params, isCounting, hefesto
        )) as Query<R>

        params.forEach { (key, value) -> query.setParameter(key, value) }

        if (isCounting) {
            return query
        }

        if (limit != null) {
            query.maxResults = limit
        }
        if (offset != null) {
            query.firstResult = offset
        }

        return query
    }

    /**
     * Creates a query with result transformation applied.
     */
    fun <R> createQuery(
        session: Session,
        selects: ConstructSelectImplementation,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        limit: Int?,
        offset: Int?,
        hefesto: Hefesto<T>
    ): Query<R> {
        return applyTransformer(
            createBaseQuery(
                session, selects, wheres, joins, joinsFetch, 
                orders, groupBy, limit, offset, false, hefesto
            )
        )
    }

    /**
     * Executes the query and returns a list of results.
     */
    fun get(
        session: Session,
        selects: ConstructSelectImplementation,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        limit: Int?,
        offset: Int?,
        hefesto: Hefesto<T>
    ): List<T> {
        return createQuery<T>(
            session, selects, wheres, joins, joinsFetch, 
            orders, groupBy, limit, offset, hefesto
        ).list()
    }

    /**
     * Executes the query and returns an Optional containing the first result.
     */
    fun findFirst(
        session: Session,
        selects: ConstructSelectImplementation,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        hefesto: Hefesto<T>
    ): Optional<T> {
        return Optional.ofNullable(
            createQuery<T>(
                session, selects, wheres, joins, joinsFetch, 
                orders, groupBy, 1, null, hefesto
            ).singleResultOrNull
        )
    }

    /**
     * Executes the query and returns a paginated result.
     */
    fun page(
        session: Session,
        selects: ConstructSelectImplementation,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        limit: Int,
        offset: Long,
        hefesto: Hefesto<T>
    ): Page<T> {
        val total = countResults(session, wheres, joins, groupBy, hefesto)

        return Page(
            createQuery<T>(
                session, selects, wheres, joins, joinsFetch, 
                orders, groupBy, limit, offset.toInt(), hefesto
            ).list(),
            offset,
            total
        )
    }

    /**
     * Counts the number of results for the query.
     */
    fun countResults(
        session: Session,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation,
        groupBy: ConstructGroupByImplementation,
        hefesto: Hefesto<T>
    ): Long {
        return createBaseQuery<Long>(
            session, ConstructSelectImplementation(), wheres, joins,
            ConstructJoinFetch(), ConstructOrderImplementation(), groupBy,
            null, null, true, hefesto
        ).singleResult
    }

    /**
     * Executes the query and returns the first result as a custom type.
     */
    fun <R> findFirstFor(
        session: Session,
        resultClass: Class<R>,
        selects: ConstructSelectImplementation,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        hefesto: Hefesto<T>
    ): R {
        if (selects.isEmpty()) {
            throw QueryException("You need put at least one select")
        }

        return applyTransformer(
            createBaseQuery(
                session, selects, wheres, joins, joinsFetch, 
                orders, groupBy, 1, null, false, hefesto
            ),
            resultClass
        ).singleResult
    }

    /**
     * Executes the query and returns a list of results as a custom type.
     */
    fun <R> findFor(
        session: Session,
        resultClass: Class<R>,
        selects: ConstructSelectImplementation,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        limit: Int?,
        offset: Int?,
        hefesto: Hefesto<T>
    ): List<R> {
        return applyTransformer(
            createBaseQuery(
                session, selects, wheres, joins, joinsFetch, 
                orders, groupBy, limit, offset, false, hefesto
            ),
            resultClass
        ).resultList
    }

    /**
     * Generates the HQL query string.
     */
    fun getQuery(
        selects: ConstructSelectImplementation,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        params: MutableMap<String, Any?>,
        isCounting: Boolean,
        hefesto: Hefesto<T>
    ): String {
        var query = if (isCounting) "select count($acronymTable)" else selects.construct(hefesto)
        query += " from $table"
        val joinsFetchQuery = if (!isCounting) joinsFetch.construct(hefesto) else ""

        return listOf(
            query, acronymTable,
            joins.construct(hefesto), joinsFetchQuery,
            wheres.construct(params, acronymTable),
            orders.construct(),
            groupBy.construct()
        ).joinToString(" ")
    }

    /**
     * Generates the HQL subquery string.
     */
    fun getSubQuery(
        selects: ConstructSelectImplementation,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        params: MutableMap<String, Any?>,
        hefesto: Hefesto<T>
    ): String {
        var query = selects.constructSubQuery(hefesto)
        query += " from $table"

        return listOf(
            query, acronymTable,
            joins.construct(hefesto),
            wheres.construct(params, acronymTable),
            orders.construct(),
            groupBy.construct()
        ).joinToString(" ")
    }

    /**
     * Applies result transformation for basic query types.
     */
    @Suppress("DEPRECATION")
    private fun <R> applyTransformer(query: Query<R>): Query<R> {
        if (isBasicClass(model)) {
            return query
        }
        return query.setTupleTransformer(FluentHibernateResultTransformer(model))
    }

    /**
     * Applies result transformation for custom result types.
     */
    @Suppress("DEPRECATION")
    private fun <R> applyTransformer(query: Query<R>, result: Class<R>): Query<R> {
        if (isBasicClass(result)) {
            return query
        }
        return query.setTupleTransformer(FluentHibernateResultTransformer(result))
    }

    /**
     * Checks if the class is a basic/primitive type that doesn't need transformation.
     */
    private fun isBasicClass(clazz: Class<*>): Boolean {
        return clazz == String::class.java || clazz == Boolean::class.javaObjectType || clazz == Char::class.javaObjectType ||
                clazz == Byte::class.javaObjectType || clazz == Short::class.javaObjectType || clazz == Int::class.javaObjectType ||
                clazz == Long::class.javaObjectType || clazz == Float::class.javaObjectType || clazz == Double::class.javaObjectType
    }
}
