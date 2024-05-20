package io.github.robertomike.hefesto.builders;

import io.github.robertomike.hefesto.actions.wheres.WhereRaw;
import io.github.robertomike.hefesto.constructors.*;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.enums.WhereOperator;
import io.github.robertomike.hefesto.models.BaseModel;
import io.github.robertomike.hefesto.utils.FluentHibernateResultTransformer;
import io.github.robertomike.hefesto.utils.Page;
import io.github.robertomike.hefesto.utils.SharedMethods;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Hefesto<T extends BaseModel>
        extends BaseBuilder<T, Session, ConstructWhereImplementation, ConstructJoinImplementation,
        ConstructOrderImplementation, ConstructSelectImplementation, ConstructGroupByImplementation, Hefesto<T>
        >
        implements SharedMethods<Hefesto<T>> {
    @Getter
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
        groupBy = new ConstructGroupByImplementation();
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

    /**
     * Unsupported method
     *
     * @param table     the table to join
     * @param joinField the field to join on
     * @param operator  the join operator
     * @return return the current instance
     */
    @Override
    public Hefesto<T> join(String table, String joinField, JoinOperator operator) {
        throw new UnsupportedOperationException("This methods are not supported");
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
     * Returns an Optional containing the first element of the result set,
     * or an empty Optional if the result set is empty.
     *
     * @return an Optional containing the first element of the result set,
     * or an empty Optional if the result set is empty.
     */
    @Override
    public Optional<T> findFirst() {
        this.limit = 1;
        return Optional.ofNullable(this.<T>createQuery().getSingleResult());
    }

    /**
     * Creates a query for the given criteria.
     *
     * @return The created query.
     */
    public <R> Query<R> createQuery() {
        return applyTransformer(createBaseQuery());
    }

    public <R> Query<R> createBaseQuery() {
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
        return this.<T>createQuery().list();
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

        return new Page<>(this.<T>createQuery().list(), offset, total);
    }

    /**
     * Counts the number of results based on the given criteria.
     *
     * @return the count of results as a Long value
     */
    public Long countResults() {
        isCounting = true;

        var total = this.<Long>createBaseQuery().getSingleResult();

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
        return applyTransformer(createBaseQuery(), resultClass).getSingleResult();
    }

    /**
     * Finds and retrieves a list of objects of the specified resultClass.
     *
     * @param resultClass the class of the objects to be retrieved
     * @return a list of objects of the specified resultClass
     */
    public <R> List<R> findFor(Class<R> resultClass) {
        return applyTransformer(createBaseQuery(), resultClass).getResultList();
    }

    public String getQuery(Map<String, Object> params) {
        var query = isCounting ? "select count(" + getAcronymTable() + ")" : selects.construct(this);
        query += " from " + getTable();
        String joinsFetchQuery = !isCounting ? joinsFetch.construct(this) : "";

        return String.join(" ",
                query, acronymTable,
                joins.construct(this), joinsFetchQuery,
                wheres.construct(params),
                orders.construct(),
                groupBy.construct()
        );
    }

    public String getSubQuery(Map<String, Object> params) {
        var query = selects.constructSubQuery(this);
        query += " from " + getTable();

        return String.join(" ",
                query, acronymTable,
                joins.construct(this),
                wheres.construct(params),
                orders.construct(),
                groupBy.construct()
        );
    }

    @Override
    public Optional<T> findFirstById(Object value) {
        where(getAcronymTable() + ".id", value);
        return findFirst();
    }
}
