package io.github.robertomike.builders;

import io.github.robertomike.enums.JoinOperator;
import io.github.robertomike.utils.ConditionalBuilder;
import io.github.robertomike.actions.Join;
import io.github.robertomike.actions.Order;
import io.github.robertomike.actions.Select;
import io.github.robertomike.constructors.ConstructJoin;
import io.github.robertomike.constructors.ConstructOrder;
import io.github.robertomike.constructors.ConstructSelect;
import io.github.robertomike.constructors.ConstructWhere;
import io.github.robertomike.enums.Operator;
import io.github.robertomike.enums.SelectOperator;
import io.github.robertomike.enums.Sort;
import io.github.robertomike.models.BaseModel;
import io.github.robertomike.utils.Page;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public abstract class BaseBuilder<
        Model extends BaseModel, SESSION, WHERE extends ConstructWhere,
        JOIN extends ConstructJoin, ORDER extends ConstructOrder, SELECT extends ConstructSelect,
        BUILDER extends BaseBuilder<Model, SESSION, WHERE, JOIN, ORDER, SELECT, BUILDER>
        >
        implements ConditionalBuilder<BUILDER, WHERE> {
    @Getter()
    protected String table;
    @Getter()
    protected ORDER orders;
    @Getter()
    protected WHERE wheres;
    @Getter()
    protected JOIN joins;
    protected Class<Model> model;
    @Setter
    protected Integer offset = null;
    @Setter
    protected Integer limit = null;
    @Getter()
    protected SELECT selects;
    @Setter
    protected static Object session;

    public BaseBuilder(Class<Model> model) {
        this.model = model;
    }

    /**
     * Returns the session associated with this object.
     *
     * @return  the session associated with this object
     */
    @SuppressWarnings("unchecked")
    public SESSION getSession() {
        return (SESSION) session;
    }

    /**
     * This method resets the selects to the value passed
     */
    @SuppressWarnings("unchecked")
    public BUILDER setSelects(String select) {
        this.selects.clear();
        selects.add(new Select(select));
        return (BUILDER) this;
    }

    /**
     * Add new select to the current list
     */
    @SuppressWarnings("unchecked")
    public BUILDER addSelect(String select) {
        selects.add(new Select(select));
        return (BUILDER) this;
    }

    /**
     * Add new select to the current list
     */
    @SuppressWarnings("unchecked")
    public BUILDER select(String... selects) {
        this.selects.clear();
        for (String select : selects) {
            this.selects.add(new Select(select));
        }
        return (BUILDER) this;
    }

    /**
     * Add new select to the current list
     */
    @SuppressWarnings("unchecked")
    public BUILDER addSelect(Select... selects) {
        this.selects.addAll(selects);
        return (BUILDER) this;
    }

    /**
     * Add new select to the current list
     */
    @SuppressWarnings("unchecked")
    public BUILDER addSelect(String select, String alias) {
        selects.add(new Select(select, alias));
        return (BUILDER) this;
    }

    /**
     * Add new select to the current list
     */
    @SuppressWarnings("unchecked")
    public BUILDER addSelect(String select, SelectOperator operator) {
        selects.add(new Select(select, operator));
        return (BUILDER) this;
    }

    /**
     * Add new select to the current list
     */
    @SuppressWarnings("unchecked")
    public BUILDER addSelect(String select, String alias, SelectOperator operator) {
        selects.add(new Select(select, alias, operator));
        return (BUILDER) this;
    }

    /**
     * Adds a join to the list of joins.
     *
     * @param  join  the join to be added
     * @return       the updated builder object
     */
    @SuppressWarnings("unchecked")
    public BUILDER join(Join join) {
        joins.add(join);
        return (BUILDER) this;
    }

    /**
     * Adds a join clause to the query.
     *
     * @param  table           the table to join
     * @param  joinField       the field to join on
     * @param  referenceField  the field to join with
     * @return                 the modified builder object
     */
    @SuppressWarnings("unchecked")
    public BUILDER join(String table, String joinField, String referenceField) {
        joins.add(Join.make(table, joinField, referenceField));
        return (BUILDER) this;
    }

    /**
     * Adds a join clause to the query.
     *
     * @param  table           the table to join
     * @param  joinField       the field to join on
     * @param  operator        the join operator
     * @return                 the modified builder object
     */
    @SuppressWarnings("unchecked")
    public BUILDER join(String table, String joinField, JoinOperator operator) {
        joins.add(Join.make(table, joinField, operator));
        return (BUILDER) this;
    }

    /**
     * Adds an order to the list of orders for sorting.
     *
     * @param  field  the field to sort on
     * @param  sort   the sort order
     * @return        the updated builder object
     */
    @SuppressWarnings("unchecked")
    public BUILDER orderBy(String field, Sort sort) {
        orders.add(new Order(field, sort));
        return (BUILDER) this;
    }

    /**
     * This allows you to order by multiple fields
     *
     * @param  fields   the fields to order by
     * @return       same instance
     */
    @SuppressWarnings("unchecked")
    public BUILDER orderBy(String... fields) {
        for (String field : fields) {
            orders.add(new Order(field));
        }
        return (BUILDER) this;
    }

    /**
     * This allows you to order by multiple fields
     *
     * @param  orders   the fields to order by
     * @return       same instance
     */
    @SuppressWarnings("unchecked")
    public BUILDER orderBy(Order... orders) {
        this.orders.addAll(orders);
        return (BUILDER) this;
    }

    /**
     * Retrieves the first model from the data source.
     *
     * @return  an Optional containing the first model, or an empty Optional if no model is found
     */
    public abstract Optional<Model> findFirst();

    /**
     * Counts the number of results.
     *
     * @return the number of results
     */
    public abstract Long countResults();

    /**
     * A description of the entire Java function.
     *
     * @param  limit  the maximum number of items to be returned
     * @param  offset the starting position of the items to be returned
     * @return        a Page object containing the requested items
     */
    public abstract Page<Model> page(int limit, long offset);

    /**
     * Retrieves a list of Model objects.
     *
     * @return a list of Model objects
     */
    public abstract List<Model> get();

    /**
     * Retrieves a page of Model objects.
     *
     * @param  limit  the maximum number of objects to retrieve
     * @return        a page of Model objects
     */
    public Page<Model> page(int limit) {
        return page(limit, 0L);
    }

    /**
     * Finds the first Model by a given field, operator, and value.
     *
     * @param  field     the field to search by
     * @param  operator  the operator to use for comparison
     * @param  value     the value to compare against
     * @return           an Optional containing the first matching Model, or an empty Optional if no match is found
     */
    public Optional<Model> findFirstBy(String field, Operator operator, Object value) {
        where(field, operator, value);
        return findFirst();
    }

    /**
     * Find the first Model object that matches the given field and value.
     *
     * @param  field     the field to search for
     * @param  value     the value to match
     * @return           an Optional containing the first matching Model, or empty if no match found
     */
    public Optional<Model> findFirstBy(String field, Object value) {
        return findFirstBy(field, Operator.EQUAL, value);
    }

    /**
     * Generates a function comment for the given function body.
     *
     * @param  value  the value to search for in the "id" field
     * @return        an optional containing the first model found, or an empty optional if no model is found
     */
    public Optional<Model> findFirstById(Object value) {
        where("id", value);
        return findFirst();
    }
}
