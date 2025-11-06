package io.github.robertomike.hefesto.builders

import io.github.robertomike.hefesto.actions.GroupBy
import io.github.robertomike.hefesto.actions.Join
import io.github.robertomike.hefesto.actions.Select
import io.github.robertomike.hefesto.constructors.*
import io.github.robertomike.hefesto.enums.JoinOperator
import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.enums.SelectOperator
import io.github.robertomike.hefesto.exceptions.QueryException
import io.github.robertomike.hefesto.models.BaseModel
import io.github.robertomike.hefesto.utils.ConditionalBuilder
import io.github.robertomike.hefesto.utils.Page
import io.github.robertomike.hefesto.utils.SortBuilder
import io.github.robertomike.hefesto.utils.CastUtils.getClassInstance
import java.util.*

/**
 * This class is the base for all the hefesto versions
 *
 * @param <Model>   define the model to return
 * @param <SESSION> define the session
 * @param <WHERE>   define the class where that is implemented
 * @param <JOIN>    define the class join that is implemented
 * @param <ORDER>   define the class order that is implemented
 * @param <SELECT>  define the class select that is implemented
 * @param <BUILDER> define the builder
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseBuilder<
        Model : BaseModel,
        SESSION,
        WHERE : ConstructWhere,
        JOIN : ConstructJoin,
        ORDER : ConstructOrder,
        SELECT : ConstructSelect,
        GROUP : ConstructGroupBy,
        BUILDER : BaseBuilder<Model, SESSION, WHERE, JOIN, ORDER, SELECT, GROUP, BUILDER>
        > : ConditionalBuilder<BUILDER>, SortBuilder<BUILDER> {

    /**
     * This contains the table name
     */
    var table: String? = null

    /**
     * This contains the class for save all selects
     */
    lateinit var selects: SELECT

    /**
     * This contains the class for save all orders
     */
    override lateinit var orders: ORDER

    /**
     * This contains the class for save all orders
     */
    lateinit var groupBy: GROUP

    /**
     * This contains the class for save all wheres
     */
    override lateinit var wheres: WHERE

    /**
     * This contains the class for save all joins
     */
    lateinit var joins: JOIN

    /**
     * This contains the class that will be returned and has the table
     */
    lateinit var model: Class<Model>

    /**
     * This contains the offset
     */
    var offset: Int? = null

    /**
     * This contains the limit
     */
    var limit: Int? = null

    /**
     * constructor
     *
     * @param model the class that will be returned
     */
    constructor(model: Class<Model>) {
        this.model = model
    }

    /**
     * Default constructor
     */
    constructor()

    /**
     * Returns the session associated with this object.
     *
     * @return the session associated with this object
     */
    protected fun getSessionInstance(): SESSION {
        if (session == null) {
            throw QueryException("Session is not set")
        }
        return session as SESSION
    }

    /**
     * This method resets the selects to the value passed
     *
     * @param select the select to add
     * @return the builder instance
     */
    fun setSelect(select: String): BUILDER {
        this.selects.clear()
        selects.add(Select(select))
        return this as BUILDER
    }

    /**
     * Add new select to the current list
     *
     * @param select the select
     * @return the current instance
     */
    fun addSelect(select: String): BUILDER {
        selects.add(Select(select))
        return this as BUILDER
    }

    /**
     * Reset and add many select to the current list
     *
     * @param selects the selects
     * @return the current instance
     */
    fun setSelects(vararg selects: String): BUILDER {
        this.selects.clear()
        for (select in selects) {
            this.selects.add(Select(select))
        }
        return this as BUILDER
    }

    /**
     * Add new select to the current list
     *
     * @param selects the selects
     * @return the current instance
     */
    fun addSelect(vararg selects: Select): BUILDER {
        this.selects.addAll(*selects)
        return this as BUILDER
    }

    /**
     * Add new select to the current list
     *
     * @param select the selects
     * @param alias  the alias for the select
     * @return the current instance
     */
    fun addSelect(select: String, alias: String): BUILDER {
        selects.add(Select(select, alias))
        return this as BUILDER
    }

    /**
     * Add new select to the current list
     *
     * @param select   the selects
     * @param operator the operator
     * @return the current instance
     */
    fun addSelect(select: String, operator: SelectOperator): BUILDER {
        selects.add(Select(select, operator))
        return this as BUILDER
    }

    /**
     * Add new select to the current list
     *
     * @param select   the selects
     * @param alias    the alias for the select
     * @param operator the operator
     * @return the current instance
     */
    fun addSelect(select: String, alias: String, operator: SelectOperator): BUILDER {
        selects.add(Select(select, alias, operator))
        return this as BUILDER
    }

    /**
     * Adds a join to the list of joins.
     *
     * @param join the join to be added
     * @return the updated builder object
     */
    fun join(join: Join): BUILDER {
        joins.add(join)
        return this as BUILDER
    }

    /**
     * Adds a join clause to the query.
     *
     * @param table          the table to join
     * @param joinField      the field to join on
     * @param referenceField the field to join with
     * @return the modified builder object
     */
    fun join(table: String, joinField: String, referenceField: String): BUILDER {
        joins.add(Join.make(table, joinField, referenceField))
        return this as BUILDER
    }

    /**
     * Adds a join clause to the query.
     *
     * @param clazz          the class that has the table to join
     * @param joinField      the field to join on
     * @param referenceField the field to join with
     * @return the modified builder object
     */
    fun join(clazz: Class<out BaseModel>, joinField: String, referenceField: String): BUILDER {
        val table = getTableFromClass(clazz)
        joins.add(Join.make(table, joinField, referenceField))
        return this as BUILDER
    }

    fun getTableFromClass(clazz: Class<out BaseModel>): String {
        return getClassInstance(clazz).getTable()
    }

    /**
     * Adds a join clause to the query.
     *
     * @param table     the table to join
     * @param joinField the field to join on
     * @param operator  the join operator
     * @return the modified builder object
     */
    open fun join(table: String, joinField: String, operator: JoinOperator): BUILDER {
        joins.add(Join.make(table, joinField, operator))
        return this as BUILDER
    }

    /**
     * This allows you to group by multiple fields
     *
     * @param fields the fields to group by
     * @return same instance
     */
    fun groupBy(vararg fields: String): BUILDER {
        for (field in fields) {
            groupBy.add(GroupBy(field))
        }
        return this as BUILDER
    }

    /**
     * This allows you to group by multiple fields
     *
     * @param groupBy the fields
     * @return same instance
     */
    fun groupBy(vararg groupBy: GroupBy): BUILDER {
        this.groupBy.addAll(*groupBy)
        return this as BUILDER
    }

    /**
     * Counts the number of results.
     *
     * @return the number of results
     */
    abstract fun countResults(): Long

    /**
     * Retrieves a page of Model objects.
     *
     * @param limit the maximum number of objects to retrieve
     * @return a page of Model objects
     */
    fun page(limit: Int): Page<Model> {
        return page(limit, 0L)
    }

    /**
     * A description of the entire Java function.
     *
     * @param limit  the maximum number of items to be returned
     * @param offset the starting position of the items to be returned
     * @return a Page object containing the requested items
     */
    abstract fun page(limit: Int, offset: Long): Page<Model>

    /**
     * Retrieves a list of Model objects.
     *
     * @return a list of Model objects
     */
    abstract fun get(): List<Model>

    /**
     * Retrieves a list of Model objects with the given selects.
     *
     * @return a list of Model objects
     */
    fun get(vararg selects: Select): List<Model> {
        addSelect(*selects)
        return get()
    }

    /**
     * Retrieves a list of Model objects with the given selects.
     *
     * @return a list of Model objects
     */
    fun get(vararg selects: String): List<Model> {
        setSelects(*selects)
        return get()
    }

    /**
     * Retrieves the first model from the data source.
     *
     * @return an Optional containing the first model, or an empty Optional if no model is found
     */
    abstract fun findFirst(): Optional<Model>

    /**
     * Finds the first Model by a given field, operator, and value.
     *
     * @param field    the field to search by
     * @param operator the operator to use for comparison
     * @param value    the value to compare against
     * @return an Optional containing the first matching Model, or an empty Optional if no match is found
     */
    fun findFirstBy(field: String, operator: Operator, value: Any?): Optional<Model> {
        where(field, operator, value)
        return findFirst()
    }

    /**
     * Find the first Model object that matches the given field and value.
     *
     * @param field the field to search for
     * @param value the value to match
     * @return an Optional containing the first matching Model, or empty if no match found
     */
    fun findFirstBy(field: String, value: Any?): Optional<Model> {
        return findFirstBy(field, Operator.EQUAL, value)
    }

    /**
     * Find the first Model object that matches the given field and value.
     *
     * @param value the value to search for in the "id" field
     * @return an optional containing the first model found, or an empty optional if no model is found
     */
    fun findFirstById(value: Any?): Optional<Model> {
        where("id", value)
        return findFirst()
    }

    /**
     * Find if exist results for the current query
     *
     * @return true if exists
     */
    fun exist(): Boolean {
        return countResults() > 0
    }

    /**
     * Find if exist results for the current query
     *
     * @param field the field to search for
     * @param operator the operator to use for comparison
     * @param value the value to search for
     * @return true if exists
     */
    fun existBy(field: String, operator: Operator, value: Any?): Boolean {
        where(field, operator, value)
        return exist()
    }

    /**
     * Find if exist results for the current query
     *
     * @param field the field to search for
     * @param value the value to search for
     * @return true if exists
     */
    fun existBy(field: String, value: Any?): Boolean {
        where(field, value)
        return exist()
    }

    companion object {
        /**
         * This contains the session for make the queries
         */
        @JvmStatic
        private var _session: Any? = null

        protected val session: Any?
            get() = _session

        @JvmStatic
        fun setSession(newSession: Any?) {
            _session = newSession
        }
    }
}
