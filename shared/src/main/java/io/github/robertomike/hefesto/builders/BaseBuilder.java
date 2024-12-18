package io.github.robertomike.hefesto.builders;

import io.github.robertomike.hefesto.actions.GroupBy;
import io.github.robertomike.hefesto.actions.Join;
import io.github.robertomike.hefesto.actions.Select;
import io.github.robertomike.hefesto.constructors.*;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.enums.SelectOperator;
import io.github.robertomike.hefesto.exceptions.QueryException;
import io.github.robertomike.hefesto.models.BaseModel;
import io.github.robertomike.hefesto.utils.ConditionalBuilder;
import io.github.robertomike.hefesto.utils.Page;
import io.github.robertomike.hefesto.utils.SortBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

import static io.github.robertomike.hefesto.utils.CastUtils.getClassInstance;

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
@SuppressWarnings("unchecked")
@NoArgsConstructor
public abstract class BaseBuilder<
        Model extends BaseModel,
        SESSION,
        WHERE extends ConstructWhere,
        JOIN extends ConstructJoin,
        ORDER extends ConstructOrder,
        SELECT extends ConstructSelect,
        GROUP extends ConstructGroupBy,
        BUILDER extends BaseBuilder<Model, SESSION, WHERE, JOIN, ORDER, SELECT, GROUP, BUILDER>
        >
        implements ConditionalBuilder<BUILDER>, SortBuilder<BUILDER> {
    /**
     * This contains the table name
     */
    @Getter
    protected String table;
    /**
     * This contains the class for save all selects
     */
    @Getter
    protected SELECT selects;
    /**
     * This contains the class for save all orders
     */
    @Getter
    protected ORDER orders;
    /**
     * This contains the class for save all orders
     */
    @Getter
    protected GROUP groupBy;
    /**
     * This contains the class for save all wheres
     */
    @Getter
    protected WHERE wheres;
    /**
     * This contains the class for save all joins
     */
    @Getter
    protected JOIN joins;
    /**
     * This contains the class that will be returned and has the table
     */
    @Getter
    protected Class<Model> model;
    /**
     * This contains the offset
     */
    @Setter
    protected Integer offset = null;
    /**
     * This contains the limit
     */
    @Setter
    protected Integer limit = null;
    /**
     * This contains the session for make the queries
     */
    @Setter
    protected static Object session;

    /**
     * constructor
     *
     * @param model the class that will be returned
     */
    public BaseBuilder(Class<Model> model) {
        this.model = model;
    }

    /**
     * Returns the session associated with this object.
     *
     * @return the session associated with this object
     */
    public SESSION getSession() {
        if (session == null) {
            throw new QueryException("Session is not set");
        }
        return (SESSION) session;
    }

    /**
     * This method resets the selects to the value passed
     *
     * @param select the select to add
     * @return the builder instance
     */
    public BUILDER setSelect(String select) {
        this.selects.clear();
        selects.add(new Select(select));
        return (BUILDER) this;
    }

    /**
     * Add new select to the current list
     *
     * @param select the select
     * @return the current instance
     */
    public BUILDER addSelect(String select) {
        selects.add(new Select(select));
        return (BUILDER) this;
    }

    /**
     * Reset and add many select to the current list
     *
     * @param selects the selects
     * @return the current instance
     */
    public BUILDER setSelects(String... selects) {
        this.selects.clear();
        for (String select : selects) {
            this.selects.add(new Select(select));
        }
        return (BUILDER) this;
    }

    /**
     * Add new select to the current list
     *
     * @param selects the selects
     * @return the current instance
     */
    public BUILDER addSelect(Select... selects) {
        this.selects.addAll(selects);
        return (BUILDER) this;
    }

    /**
     * Add new select to the current list
     *
     * @param select the selects
     * @param alias  the alias for the select
     * @return the current instance
     */
    public BUILDER addSelect(String select, String alias) {
        selects.add(new Select(select, alias));
        return (BUILDER) this;
    }

    /**
     * Add new select to the current list
     *
     * @param select   the selects
     * @param operator the operator
     * @return the current instance
     */
    public BUILDER addSelect(String select, SelectOperator operator) {
        selects.add(new Select(select, operator));
        return (BUILDER) this;
    }

    /**
     * Add new select to the current list
     *
     * @param select   the selects
     * @param alias    the alias for the select
     * @param operator the operator
     * @return the current instance
     */
    public BUILDER addSelect(String select, String alias, SelectOperator operator) {
        selects.add(new Select(select, alias, operator));
        return (BUILDER) this;
    }

    /**
     * Adds a join to the list of joins.
     *
     * @param join the join to be added
     * @return the updated builder object
     */
    public BUILDER join(Join join) {
        joins.add(join);
        return (BUILDER) this;
    }

    /**
     * Adds a join clause to the query.
     *
     * @param table          the table to join
     * @param joinField      the field to join on
     * @param referenceField the field to join with
     * @return the modified builder object
     */
    public BUILDER join(String table, String joinField, String referenceField) {
        joins.add(Join.make(table, joinField, referenceField));
        return (BUILDER) this;
    }

    /**
     * Adds a join clause to the query.
     *
     * @param clazz          the class that has the table to join
     * @param joinField      the field to join on
     * @param referenceField the field to join with
     * @return the modified builder object
     */
    public BUILDER join(Class<BaseModel> clazz, String joinField, String referenceField) {
        var table = getTableFromClass(clazz);
        joins.add(Join.make(table, joinField, referenceField));
        return (BUILDER) this;
    }

    public String getTableFromClass(Class<BaseModel> clazz) {
        return getClassInstance(clazz).getTable();
    }

    /**
     * Adds a join clause to the query.
     *
     * @param table     the table to join
     * @param joinField the field to join on
     * @param operator  the join operator
     * @return the modified builder object
     */
    public BUILDER join(String table, String joinField, JoinOperator operator) {
        joins.add(Join.make(table, joinField, operator));
        return (BUILDER) this;
    }

    /**
     * This allows you to group by multiple fields
     *
     * @param fields the fields to group by
     * @return same instance
     */
    public BUILDER groupBy(String... fields) {
        for (String field : fields) {
            groupBy.add(new GroupBy(field));
        }
        return (BUILDER) this;
    }

    /**
     * This allows you to group by multiple fields
     *
     * @param groupBy the fields
     * @return same instance
     */
    public BUILDER groupBy(GroupBy... groupBy) {
        this.groupBy.addAll(groupBy);
        return (BUILDER) this;
    }

    /**
     * Counts the number of results.
     *
     * @return the number of results
     */
    public abstract Long countResults();

    /**
     * Retrieves a page of Model objects.
     *
     * @param limit the maximum number of objects to retrieve
     * @return a page of Model objects
     */
    public Page<Model> page(int limit) {
        return page(limit, 0L);
    }

    /**
     * A description of the entire Java function.
     *
     * @param limit  the maximum number of items to be returned
     * @param offset the starting position of the items to be returned
     * @return a Page object containing the requested items
     */
    public abstract Page<Model> page(int limit, long offset);

    /**
     * Retrieves a list of Model objects.
     *
     * @return a list of Model objects
     */
    public abstract List<Model> get();

    /**
     * Retrieves a list of Model objects with the given selects.
     *
     * @return a list of Model objects
     */
    public List<Model> get(Select... selects) {
        addSelect(selects);
        return get();
    }

    /**
     * Retrieves a list of Model objects with the given selects.
     *
     * @return a list of Model objects
     */
    public List<Model> get(String... selects) {
        setSelects(selects);
        return get();
    }

    /**
     * Retrieves the first model from the data source.
     *
     * @return an Optional containing the first model, or an empty Optional if no model is found
     */
    public abstract Optional<Model> findFirst();

    /**
     * Finds the first Model by a given field, operator, and value.
     *
     * @param field    the field to search by
     * @param operator the operator to use for comparison
     * @param value    the value to compare against
     * @return an Optional containing the first matching Model, or an empty Optional if no match is found
     */
    public Optional<Model> findFirstBy(String field, Operator operator, Object value) {
        where(field, operator, value);
        return findFirst();
    }

    /**
     * Find the first Model object that matches the given field and value.
     *
     * @param field the field to search for
     * @param value the value to match
     * @return an Optional containing the first matching Model, or empty if no match found
     */
    public Optional<Model> findFirstBy(String field, Object value) {
        return findFirstBy(field, Operator.EQUAL, value);
    }

    /**
     * Find the first Model object that matches the given field and value.
     *
     * @param value the value to search for in the "id" field
     * @return an optional containing the first model found, or an empty optional if no model is found
     */
    public Optional<Model> findFirstById(Object value) {
        where("id", value);
        return findFirst();
    }

    /**
     * Find if exist results for the current query
     *
     * @return true if exists
     */
    public boolean exist() {
        return countResults() > 0;
    }

    /**
     * Find if exist results for the current query
     *
     * @param field the field to search for
     * @param operator the operator to use for comparison
     * @param value the value to search for
     * @return true if exists
     */
    public boolean existBy(String field, Operator operator, Object value) {
        where(field, operator, value);
        return exist();
    }

    /**
     * Find if exist results for the current query
     *
     * @param field the field to search for
     * @param value the value to search for
     * @return true if exists
     */
    public boolean existBy(String field, Object value) {
        where(field, value);
        return exist();
    }
}
