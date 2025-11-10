package io.github.robertomike.hefesto.builders

import io.github.robertomike.hefesto.constructors.*
import io.github.robertomike.hefesto.models.BaseModel
import io.github.robertomike.hefesto.utils.Page
import org.hibernate.QueryException
import org.hibernate.Session
import org.hibernate.query.Query
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import jakarta.persistence.criteria.Subquery
import java.util.*

/**
 * Internal executor class responsible for query construction and execution.
 * Separates the concern of query building from query execution.
 * 
 * This class is internal and should not be used directly by users.
 * All interactions should go through the Hefesto facade.
 *
 * @param <T> the entity type
 */
internal class HefestoExecutor<T : BaseModel>(
    private val model: Class<T>,
    private val originalModel: Class<*>?,
    private val customResultSubQuery: Class<*>?
) {
    
    /**
     * Creates a query for the given criteria.
     *
     * @return The created query.
     */
    @Suppress("UNCHECKED_CAST")
    fun createQuery(
        session: Session,
        selects: ConstructSelectImplementation<T>,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation<T>,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        limit: Int?,
        offset: Int?
    ): Query<T> {
        val cb = session.criteriaBuilder
        val cr = cb.createQuery(model)
        val root = getRoot(cr)

        joinsFetch.construct(root)

        joins.construct(root)
        selects.setJoins(joins.joins)
        if (originalModel == null) {
            selects.construct(root as Root<T>, cr, cb)
        } else {
            selects.multiSelect(root, cr, cb, isProjection = true)
        }
        wheres.setJoins(joins.joins).setJoinConditions(joins.joinConditions).construct(cb, cr, root)
        orders.setJoins(joins.joins).construct(cb, cr, root)
        groupBy.construct(cr, root)

        val query = session.createQuery(cr)

        if (limit != null) {
            query.maxResults = limit
        }
        if (offset != null) {
            query.firstResult = offset
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
    fun createSubQuery(
        cr: CriteriaQuery<*>,
        parentRoot: Root<*>,
        cb: CriteriaBuilder,
        parentJoins: Map<String, jakarta.persistence.criteria.Join<*, *>>,
        selects: ConstructSelectImplementation<T>,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation<T>,
        groupBy: ConstructGroupByImplementation
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
        wheres.setJoins(allJoins).setJoinConditions(joins.joinConditions).constructSubQuery(sub, cb, root, parentRoot)
        selects.setJoins(allJoins).constructSubQuery(root, sub)
        groupBy.construct(cr, root)

        // Note: Hibernate Criteria API's Subquery doesn't support setMaxResults/setFirstResult
        // Limits in subqueries are typically handled at the SQL level
        // For now, we pass the parameters but cannot apply them directly
        // This is a known limitation of Hibernate's Criteria API

        return sub
    }

    /**
     * Retrieves a list of objects.
     *
     * @return a list of objects
     */
    fun get(
        session: Session,
        selects: ConstructSelectImplementation<T>,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation<T>,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        limit: Int?,
        offset: Int?
    ): List<T> {
        return createQuery(session, selects, wheres, joins, joinsFetch, orders, groupBy, limit, offset).resultList
    }

    /**
     * Returns an Optional containing the first element of the result set,
     * or an empty Optional if the result set is empty.
     *
     * @return an Optional containing the first element of the result set,
     * or an empty Optional if the result set is empty.
     */
    fun findFirst(
        session: Session,
        selects: ConstructSelectImplementation<T>,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation<T>,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation
    ): Optional<T> {
        return Optional.ofNullable(
            createQuery(session, selects, wheres, joins, joinsFetch, orders, groupBy, 1, null).singleResultOrNull
        )
    }

    /**
     * Retrieves a page of results from the database based on the specified limit and offset.
     *
     * @param limit  the maximum number of results to retrieve
     * @param offset the starting position of the results
     * @return a Page object containing the retrieved results, the offset used, and the total number of results
     */
    fun page(
        session: Session,
        selects: ConstructSelectImplementation<T>,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation<T>,
        joinsFetch: ConstructJoinFetch,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        limit: Int,
        offset: Long
    ): Page<T> {
        val total = countResults(session, wheres, joins, groupBy)
        val results = createQuery(
            session, selects, wheres, joins, joinsFetch, orders, groupBy, 
            limit, offset.toInt()
        ).resultList

        return Page(results, offset, total)
    }

    /**
     * Counts the number of results based on the given criteria.
     *
     * @return the count of results as a Long value
     */
    fun countResults(
        session: Session,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation<T>,
        groupBy: ConstructGroupByImplementation
    ): Long {
        val cb = session.criteriaBuilder
        val cr = cb.createQuery(Long::class.java)
        val root = cr.from(model)

        cr.select(cb.count(root))
        joins.construct(root)
        wheres.setJoins(joins.joins).setJoinConditions(joins.joinConditions).construct(cb, cr, root)
        groupBy.construct(cr, root)

        return session.createQuery(cr).singleResult
    }

    /**
     * Find the first result of the specified result class.
     *
     * @param resultClass the class of the result to be returned
     * @return the first result of the specified class, or null if no result is found
     */
    fun <R> findFirstFor(
        session: Session,
        resultClass: Class<R>,
        selects: ConstructSelectImplementation<T>,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation<T>,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        offset: Int?
    ): R {
        if (selects.isEmpty()) {
            throw QueryException("You need put at least one select")
        }

        val cr = commonConstructForCustomResult(session, resultClass, selects, wheres, joins, orders, groupBy)
        val query = session.createQuery(cr)
        query.maxResults = 1
        
        if (offset != null) {
            query.firstResult = offset
        }

        return query.singleResult
    }

    /**
     * Finds and retrieves a list of objects of the specified resultClass.
     *
     * @param resultClass the class of the objects to be retrieved
     * @return a list of objects of the specified resultClass
     */
    fun <R> findFor(
        session: Session,
        resultClass: Class<R>,
        selects: ConstructSelectImplementation<T>,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation<T>,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation,
        limit: Int?,
        offset: Int?
    ): List<R> {
        if (selects.isEmpty()) {
            throw QueryException("You need put at least one select")
        }

        val cr = commonConstructForCustomResult(session, resultClass, selects, wheres, joins, orders, groupBy)
        val query = session.createQuery(cr)
        
        if (limit != null) {
            query.maxResults = limit
        }
        if (offset != null) {
            query.firstResult = offset
        }
        
        return query.resultList
    }

    /**
     * Generates a common criteria query for custom result.
     *
     * @param resultClass the class of the result
     * @return the generated criteria query
     */
    private fun <R> commonConstructForCustomResult(
        session: Session,
        resultClass: Class<R>,
        selects: ConstructSelectImplementation<T>,
        wheres: ConstructWhereImplementation,
        joins: ConstructJoinImplementation<T>,
        orders: ConstructOrderImplementation,
        groupBy: ConstructGroupByImplementation
    ): CriteriaQuery<R> {
        val cb = session.criteriaBuilder
        val cr = cb.createQuery(resultClass)
        val root = cr.from(model)

        joins.construct(root)
        selects.setJoins(joins.joins)
            .multiSelect(root, cr, cb)
        wheres.setJoins(joins.joins)
            .setJoinConditions(joins.joinConditions)
            .construct(cb, cr, root)
        orders.setJoins(joins.joins)
            .construct(cb, cr, root)
        groupBy.construct(cr, root)

        return cr
    }
}
