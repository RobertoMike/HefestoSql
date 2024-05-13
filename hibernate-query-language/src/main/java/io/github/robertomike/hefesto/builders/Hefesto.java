package io.github.robertomike.hefesto.builders;

import io.github.robertomike.hefesto.actions.Join;
import io.github.robertomike.hefesto.actions.JoinFetch;
import io.github.robertomike.hefesto.actions.wheres.WhereField;
import io.github.robertomike.hefesto.actions.wheres.WhereRaw;
import io.github.robertomike.hefesto.constructors.*;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.enums.WhereOperator;
import io.github.robertomike.hefesto.models.BaseModel;
import io.github.robertomike.hefesto.utils.FluentHibernateResultTransformer;
import io.github.robertomike.hefesto.utils.Page;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.JoinType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Hefesto<T extends BaseModel>
        extends BaseBuilder<T, Session, ConstructWhereImplementation, ConstructJoinImplementation, ConstructOrderImplementation, ConstructSelectImplementation, Hefesto<T>> {
    private final ConstructJoinFetch joinsFetch = new ConstructJoinFetch();
    private Class<? extends BaseModel> originalModel = null;
    @Setter
    @Getter
    private String acronymTable;
    private boolean isCounting = false;

    public Hefesto(Class<T> model) {
        super(model);
        setup();
    }

    private void setup() {
        orders = new ConstructOrderImplementation();
        wheres = new ConstructWhereImplementation();
        joins = new ConstructJoinImplementation();
        selects = new ConstructSelectImplementation();
        table = (originalModel != null ? originalModel : model).getSimpleName();
        acronymTable = getTable().toLowerCase();
    }

    public Hefesto(Class<? extends BaseModel> modelEntity, Class<T> dto) {
        super(dto);
        originalModel = modelEntity;
        setup();
    }

    /**
     * Create a new instance of the Hefesto class with the given model.
     *
     * @param model the model class to be used
     * @return a new instance of Hefesto with the given model
     */
    public static <T extends BaseModel> Hefesto<T> make(Class<T> model) {
        return new Hefesto<>(model);
    }

    @Override
    public Hefesto<T> join(String table, String joinField, String referenceField) {
        throw new UnsupportedOperationException("This methods are not supported");
    }

    @Override
    public Hefesto<T> join(String table, String joinField, JoinOperator operator) {
        throw new UnsupportedOperationException("This methods are not supported");
    }

    /**
     * Adds a join to load in the query.
     *
     * @param relationship the relationship to join on
     * @return the updated Hefesto object
     */
    public Hefesto<T> join(String relationship) {
        joins.add(Join.make(relationship));
        return this;
    }

    /**
     * Adds a join to load in the query.
     *
     * @param relationship the relationship to join on
     * @return the updated Hefesto object
     */
    public Hefesto<T> join(String relationship, String alias) {
        joins.add(new Join(relationship, null, null, alias));
        return this;
    }

    /**
     * Adds a join to load in the query.
     *
     * @param operator the join operator
     * @return the updated Hefesto object
     */
    public Hefesto<T> join(String relationship, JoinOperator operator) {
        joins.add(Join.make(relationship, operator));
        return this;
    }

    /**
     * This method adds a where field to the query that allows you to filter by a field of a join, the same root or the parent when is a subQuery
     *
     * @param parentField could be parent field or a join field
     */
    public Hefesto<T> whereField(String field, String parentField) {
        getWheres().add(new WhereField(field, parentField));
        return this;
    }

    /**
     * This method adds a where that allow you to pass lambda and return a Predicate
     */
    public Hefesto<T> whereRaw(WhereRaw custom) {
        getWheres().add(custom);
        return this;
    }

    /**
     * This method adds a where that allow you to pass lambda and return a Predicate
     */
    public Hefesto<T> whereRaw(String raw) {
        getWheres().add(new WhereRaw(raw));
        return this;
    }

    /**
     * This method adds a where with or that allow you to pass lambda and return a Predicate
     */
    public Hefesto<T> orWhereRaw(String raw) {
        getWheres().add(new WhereRaw(raw, WhereOperator.OR));
        return this;
    }

    /**
     * This method adds a where field to the query that allows you to filter by a field of a join, the same root or the parent when is a subQuery
     *
     * @param operator    not all operators are supported, only LIKE, NOT_LIKE, EQUAL, DIFF, GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL
     * @param parentField could be parent field or a join field
     */
    public Hefesto<T> whereField(String field, Operator operator, String parentField) {
        getWheres().add(new WhereField(field, operator, parentField));
        return this;
    }

    /**
     * This method will allow you to preload the relationships you want to fetch
     */
    public Hefesto<T> with(JoinFetch... relationship) {
        joinsFetch.addAll(relationship);
        return this;
    }

    /**
     * This method will allow you to preload the relationships you want to fetch
     */
    public Hefesto<T> with(String... relationships) {
        for (var relationship : relationships) {
            joinsFetch.add(JoinFetch.make(relationship));
        }
        return this;
    }

    /**
     * This method will allow you to preload the relationships you want to fetch with the join type
     */
    public Hefesto<T> with(String relationship, JoinType joinType) {
        joinsFetch.add(JoinFetch.make(relationship, joinType));
        return this;
    }

    /**
     * Returns an Optional containing the first element of the result set,
     * or an empty Optional if the result set is empty.
     *
     * @return an Optional containing the first element of the result set,
     * or an empty Optional if the result set is empty.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> findFirst() {
        this.limit = 1;
        return Optional.ofNullable(this.<T>createQueryAndApplyTransform().getSingleResult());
    }

    /**
     * Creates a query for the given criteria.
     *
     * @return The created query.
     */
    public <R> Query<R> createQueryAndApplyTransform() {
        return applyTransformer(createQuery());
    }

    public <R> Query<R> createQuery() {
        Map<String, Object> params = new HashMap<>();

        @SuppressWarnings({"unchecked"})
        Query<R> query = getSession().createQuery(getQuery(params));

        params.forEach(query::setParameter);

        if (isCounting) {
            return query;
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }
        if (offset != null) {
            query.setFirstResult(offset);
        }

        return query;
    }

    @SuppressWarnings("deprecation")
    private <R> Query<R> applyTransformer(Query<R> query) {
        if (isBasicClass(model)) {
            return query;
        }
        return query.setResultTransformer(new FluentHibernateResultTransformer(model));
    }

    private boolean isBasicClass(Class<?> clazz) {
        return clazz == String.class || clazz == Boolean.class || clazz == Character.class ||
                clazz == Byte.class || clazz == Short.class || clazz == Integer.class ||
                clazz == Long.class || clazz == Float.class || clazz == Double.class;
    }

    @SuppressWarnings("deprecation")
    private <R> Query<R> applyTransformer(Query<R> query, Class<R> result) {
        if (isBasicClass(result)) {
            return query;
        }
        return query.setResultTransformer(new FluentHibernateResultTransformer(result));
    }

    /**
     * Retrieves a list of objects.
     *
     * @return a list of objects
     */
    public List<T> get() {
        return this.<T>createQueryAndApplyTransform().list();
    }

    /**
     * Retrieves a page of results from the database based on the specified limit and offset.
     *
     * @param limit  the maximum number of results to retrieve
     * @param offset the starting position of the results
     * @return a Page object containing the retrieved results, the offset used, and the total number of results
     */
    public Page<T> page(int limit, long offset) {
        this.offset = Math.toIntExact(offset);
        this.limit = limit;

        var total = countResults();

        return new Page<>(this.<T>createQueryAndApplyTransform().list(), offset, total);
    }

    /**
     * Counts the number of results based on the given criteria.
     *
     * @return the count of results as a Long value
     */
    public Long countResults() {
        isCounting = true;

        var total = this.<Long>createQuery().getSingleResult();

        isCounting = false;

        return total;
    }

    /**
     * Find the first result of the specified result class.
     *
     * @param resultClass the class of the result to be returned
     * @return the first result of the specified class, or null if no result is found
     */
    public <R> R findFirstFor(Class<R> resultClass) {
        if (selects.isEmpty()) {
            throw new QueryException("You need put at least one select");
        }

        this.limit = 1;
        return applyTransformer(createQuery(), resultClass).getSingleResult();
    }

    /**
     * Finds and retrieves a list of objects of the specified resultClass.
     *
     * @param resultClass the class of the objects to be retrieved
     * @return a list of objects of the specified resultClass
     */
    public <R> List<R> findFor(Class<R> resultClass) {
        return applyTransformer(createQuery(), resultClass).getResultList();
    }

    public String getQuery(Map<String, Object> params) {
        var query = isCounting ? "select count(" + getAcronymTable() + ")" : selects.construct(this);
        query += " from " + getTable();
        String joinsFetchQuery = !isCounting ? joinsFetch.construct(this) : "";

        return String.join(" ",
                query, acronymTable,
                joins.construct(this), joinsFetchQuery,
                wheres.construct(params), orders.construct()
        );
    }

    public String getSubQuery(Map<String, Object> params) {
        var query = selects.constructSubQuery(this);
        query += " from " + getTable();

        return String.join(" ",
                query, acronymTable,
                joins.construct(this),
                wheres.construct(params), orders.construct()
        );
    }

    @Override
    public Optional<T> findFirstById(Object value) {
        where(getAcronymTable() + ".id", value);
        return findFirst();
    }
}
