package io.github.robertomike.hefesto.builders;

import io.github.robertomike.hefesto.actions.Join;
import io.github.robertomike.hefesto.actions.wheres.WhereCustom;
import io.github.robertomike.hefesto.constructors.*;
import io.github.robertomike.hefesto.enums.WhereOperator;
import io.github.robertomike.hefesto.models.BaseModel;
import io.github.robertomike.hefesto.utils.Page;
import io.github.robertomike.hefesto.utils.SharedMethods;
import lombok.Getter;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Hefesto<T extends BaseModel>
        extends BaseBuilder<T, Session, ConstructWhereImplementation,
        ConstructJoinImplementation<T>, ConstructOrderImplementation, ConstructSelectImplementation<T>,
        ConstructGroupByImplementation, Hefesto<T>
        >
        implements SharedMethods<Hefesto<T>> {
    @Getter
    private final ConstructJoinFetch joinsFetch = new ConstructJoinFetch();
    private Class<?> originalModel = null;
    private Class<?> customResultSubQuery = null;

    public Hefesto(Class<T> model) {
        super(model);
        orders = new ConstructOrderImplementation();
        wheres = new ConstructWhereImplementation();
        joins = new ConstructJoinImplementation<>();
        selects = new ConstructSelectImplementation<>();
        groupBy = new ConstructGroupByImplementation();
    }

    public Hefesto(Class<? extends BaseModel> modelEntity, Class<T> dto) {
        this(dto);
        this.originalModel = modelEntity;
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
     * Create a new instance of the Hefesto class with the given model.
     *
     * @param model the model class to be used
     * @param dto the model class that will be the result
     * @return a new instance of Hefesto with the given model
     */
    public static <T extends BaseModel> Hefesto<T> make(Class<? extends BaseModel> model, Class<T> dto) {
        return new Hefesto<>(model, dto);
    }

    /**
     * Adds a join to load in the query.
     *
     * @param field the field to join on
     * @param alias this alias is not for the query is only when you need to use the join in a where condition or select
     * @return the updated Hefesto object
     */
    public Hefesto<T> join(String field, String alias) {
        joins.add(Join.make(field, alias));
        return this;
    }

    /**
     * This method adds a where that allow you to pass lambda and return a Predicate
     */
    public Hefesto<T> whereCustom(WhereCustom.Custom custom) {
        getWheres().add(new WhereCustom(custom));
        return this;
    }

    /**
     * This method adds a where with or that allow you to pass lambda and return a Predicate
     */
    public Hefesto<T> orWhereCustom(WhereCustom.Custom custom) {
        getWheres().add(new WhereCustom(custom, WhereOperator.OR));
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
        return Optional.ofNullable(createQuery().getSingleResult());
    }

    /**
     * Creates a query for the given criteria.
     *
     * @return The created query.
     */
    @SuppressWarnings("unchecked")
    protected Query<T> createQuery() {
        var cb = getSession().getCriteriaBuilder();
        var cr = cb.createQuery(model);
        var root = getRoot(cr);

        joinsFetch.construct(root);

        joins.construct(root);
        selects.setJoins(joins.getJoins());
        if (originalModel == null) {
            selects.construct((Root<T>) root, cr, cb);
        } else {
            selects.multiSelect(root, cr, cb);
        }
        wheres.setJoins(joins.getJoins()).construct(cb, cr, root);
        orders.setJoins(joins.getJoins()).construct(cb, cr, root);
        groupBy.construct(cr, root);

        var query = getSession().createQuery(cr);

        if (limit != null) {
            query.setMaxResults(limit);
        }
        if (offset != null) {
            query.setFirstResult(offset);
        }

        return query;
    }

    /**
     * Retrieves the root entity for the given CriteriaQuery.
     *
     * @param cr the CriteriaQuery object to retrieve the root from
     * @return the root entity for the given CriteriaQuery
     */
    private Root<?> getRoot(CriteriaQuery<T> cr) {
        if (originalModel == null) {
            return cr.from(model);
        }
        return cr.from(originalModel);
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
    public Subquery<?> getSubQuery(CriteriaQuery<?> cr, Root<?> parentRoot, CriteriaBuilder cb, Map<String, javax.persistence.criteria.Join<?, ?>> parentJoins) {
        var sub = customResultSubQuery != null ? cr.subquery(customResultSubQuery) : cr.subquery(model);
        var root = sub.from(model);

        joins.construct(root);
        var allJoins = new HashMap<>(parentJoins);
        allJoins.putAll(joins.getJoins());
        wheres.setJoins(allJoins).constructSubQuery(sub, cb, root, parentRoot);
        selects.setJoins(allJoins).constructSubQuery(root, sub);
        groupBy.construct(cr, root);

        return sub;
    }

    public Hefesto<T> setCustomResultForSubQuery(Class<?> customResultSubQuery) {
        this.customResultSubQuery = customResultSubQuery;
        return this;
    }

    public boolean hasCustomResultForSubQuery() {
        return customResultSubQuery != null;
    }

    /**
     * Retrieves a list of objects.
     *
     * @return a list of objects
     */
    public List<T> get() {
        return createQuery().getResultList();
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

        return new Page<>(createQuery().getResultList(), offset, total);
    }

    /**
     * Counts the number of results based on the given criteria.
     *
     * @return the count of results as a Long value
     */
    public Long countResults() {
        var cb = getSession().getCriteriaBuilder();
        var cr = cb.createQuery(Long.class);
        var root = cr.from(model);

        cr.select(cb.count(root));
        joins.construct(root);
        wheres.setJoins(joins.getJoins()).construct(cb, cr, root);
        groupBy.construct(cr, root);

        return getSession().createQuery(cr).getSingleResult();
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

        var cr = commonConstructForCustomResult(resultClass);

        var query = getSession().createQuery(cr);
        query.setMaxResults(1);

        return query.getSingleResult();
    }

    /**
     * Finds and retrieves a list of objects of the specified resultClass.
     *
     * @param resultClass the class of the objects to be retrieved
     * @return a list of objects of the specified resultClass
     */
    public <R> List<R> findFor(Class<R> resultClass) {
        if (selects.isEmpty()) {
            throw new QueryException("You need put at least one select");
        }

        var cr = commonConstructForCustomResult(resultClass);

        return getSession().createQuery(cr).getResultList();
    }

    /**
     * Generates a common criteria query for custom result.
     *
     * @param resultClass the class of the result
     * @return the generated criteria query
     */
    private <R> CriteriaQuery<R> commonConstructForCustomResult(Class<R> resultClass) {
        var cb = getSession().getCriteriaBuilder();
        var cr = cb.createQuery(resultClass);
        var root = cr.from(model);

        joins.construct(root);
        selects.setJoins(joins.getJoins())
                .multiSelect(root, cr, cb);
        wheres.setJoins(joins.getJoins())
                .construct(cb, cr, root);
        orders.setJoins(joins.getJoins())
                .construct(cb, cr, root);
        groupBy.construct(cr, root);

        return cr;
    }
}
