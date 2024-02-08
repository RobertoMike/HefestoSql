package io.github.robertomike.hefesto.constructors;

import io.github.robertomike.hefesto.actions.wheres.*;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.exceptions.QueryException;
import io.github.robertomike.hefesto.exceptions.UnsupportedOperationException;
import io.github.robertomike.hefesto.utils.CastUtils;
import jakarta.persistence.criteria.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.robertomike.hefesto.utils.HibernateUtils.DOT_REGEX;
import static io.github.robertomike.hefesto.utils.HibernateUtils.getFieldFrom;

public class ConstructWhereImplementation extends ConstructWhere {
    protected CriteriaBuilder cb;
    protected CriteriaQuery<?> cr;
    protected Root<?> root;
    private Map<String, Join<?, ?>> joins = new HashMap<>();
    private Root<?> parentRoot = null;

    public ConstructWhereImplementation setJoins(Map<String, Join<?, ?>> joins) {
        this.joins = joins;
        return this;
    }

    public void construct(CriteriaBuilder cb, CriteriaQuery<?> cr, Root<?> root) {
        if (isEmpty()) {
            return;
        }

        this.cr = cr;
        this.cb = cb;
        this.root = root;

        cr.where(transform(items));
    }

    private Predicate transform(List<? extends BaseWhere> wheres) {
        final Predicate[] lastPredicate = {null};

        wheres.forEach(value -> {
            Predicate predicate = getPredicateFromWhere(value);

            if (lastPredicate[0] == null) {
                lastPredicate[0] = predicate;
                return;
            }

            lastPredicate[0] = applyWhereOperation(value, lastPredicate[0], predicate);
        });

        return lastPredicate[0];
    }

    public Predicate getPredicateFromWhere(BaseWhere where) {
        if (where instanceof CollectionWhere collectionWhere) {
            return transform(collectionWhere.getWheres());
        }

        if (where instanceof WhereCustom whereCustom) {
            return whereCustom.getCustom().call(cb, cr, root, joins, parentRoot);
        }

        if (where instanceof WhereExist whereExist) {
            return applyWhereExist(whereExist);
        }

        if (where instanceof WhereField whereField) {
            return constructWhereField(whereField);
        }

        if (where instanceof Where normalWhere) {
            return constructWhere(normalWhere);
        }

        throw new QueryException("Invalid class extended from BaseWhere: " + where.getClass());
    }

    private Predicate constructWhereField(WhereField where) {
        Predicate predicate;
        From<?, ?> from = root;
        From<?, ?> parentFrom = parentRoot == null ? root : parentRoot;
        String parentField = where.getParentField();
        String field = where.getField();

        if (field.contains(".") && joins.containsKey(field.split(DOT_REGEX)[0])) {
            var split = field.split(DOT_REGEX);
            from = joins.get(split[0]);
            field = split[1];
        }

        if (parentField.contains(".") && joins.containsKey(field.split(DOT_REGEX)[0])) {
            var split = parentField.split(DOT_REGEX);
            parentFrom = joins.get(split[0]);
            parentField = split[1];
        }

        switch (where.getOperator()) {
            case LIKE -> predicate = cb.like(getFieldFrom(from, field), getFieldFrom(parentFrom, parentField));
            case NOT_LIKE -> predicate = cb.notLike(getFieldFrom(from, field), getFieldFrom(parentFrom, parentField));

            case EQUAL -> predicate = cb.equal(getFieldFrom(from, field), getFieldFrom(parentFrom, parentField));
            case DIFF -> predicate = cb.notEqual(getFieldFrom(from, field), getFieldFrom(parentFrom, parentField));

            case GREATER -> predicate = cb.gt(getFieldFrom(from, field), getFieldFrom(parentFrom, parentField));
            case LESS -> predicate = cb.lt(getFieldFrom(from, field), getFieldFrom(parentFrom, parentField));
            case GREATER_OR_EQUAL ->
                    predicate = cb.ge(getFieldFrom(from, field), getFieldFrom(parentFrom, parentField));
            case LESS_OR_EQUAL -> predicate = cb.le(getFieldFrom(from, field), getFieldFrom(parentFrom, parentField));

            default -> throw new UnsupportedOperationException("Unsupported operator: " + where.getOperator());
        }

        return predicate;
    }

    private Predicate applyWhereExist(WhereExist whereExist) {
        Hefesto<?> subBuilder = (Hefesto<?>) whereExist.getSubQuery();

        var subQuery = subBuilder.getSubQuery(cr, root, cb, joins);

        Predicate exist;

        if (whereExist.isExists()) {
            exist = cb.exists(subQuery);
        } else {
            exist = cb.not(cb.exists(subQuery));
        }

        return exist;
    }

    private Predicate constructWhere(Where where) {
        Predicate predicate;
        From<?, ?> from = root;
        String field = where.getField();

        if (field.contains(".") && joins.containsKey(field.split(DOT_REGEX)[0])) {
            var split = field.split(DOT_REGEX);
            from = joins.get(split[0]);
            field = split[1];
        }

        switch (where.getOperator()) {
            case LIKE -> predicate = cb.like(getFieldFrom(from, field), where.getValue().toString());
            case NOT_LIKE -> predicate = cb.notLike(getFieldFrom(from, field), where.getValue().toString());

            case EQUAL -> predicate = cb.equal(getFieldFrom(from, field), where.getValue());
            case DIFF -> predicate = cb.notEqual(getFieldFrom(from, field), where.getValue());

            case GREATER -> {
                Path<Number> path = getFieldFrom(from, field);
                var value = getTransformedValue(where.getValue(), path);
                predicate = cb.gt(path, value);
            }
            case LESS -> {
                Path<Number> path = getFieldFrom(from, field);
                var value = getTransformedValue(where.getValue(), path);
                predicate = cb.lt(path, value);
            }
            case GREATER_OR_EQUAL -> {
                Path<Number> path = getFieldFrom(from, field);
                var value = getTransformedValue(where.getValue(), path);
                predicate = cb.ge(path, value);
            }
            case LESS_OR_EQUAL -> {
                Path<Number> path = getFieldFrom(from, field);
                var value = getTransformedValue(where.getValue(), path);
                predicate = cb.le(path, value);
            }

            case IS_NULL -> predicate = cb.isNull(getFieldFrom(from, field));
            case IS_NOT_NULL -> predicate = cb.isNotNull(getFieldFrom(from, field));

            case IN -> predicate = applyWhereIn(where, from, field);
            case NOT_IN -> predicate = cb.not(applyWhereIn(where, from, field));

            case FIND_IN_SET -> predicate = cb.greaterThan(
                    getPredicateForFindInSet(where, getFieldFrom(from, field)),
                    cb.literal(0)
            );
            case NOT_FIND_IN_SET -> predicate = cb.equal(
                    getPredicateForFindInSet(where, getFieldFrom(from, field)),
                    cb.literal(0)
            );
            default -> throw new UnsupportedOperationException("Unsupported operator: " + where.getOperator());
        }

        return predicate;
    }

    private Expression<Integer> getPredicateForFindInSet(Where where, Path<Object> path) {
        return cb.function("find_in_set", Integer.class, cb.literal(where.getValue().toString()), path);
    }

    private Predicate applyWhereIn(Where where, From<?, ?> from, String field) {
        var in = cb.in(getFieldFrom(from, field));

        if (where.getValue() instanceof Object[] values) {
            for (var value : values) {
                in.value(value);
            }
            return in;
        }

        if (where.getValue() instanceof Iterable<?> values) {
            values.forEach(in::value);
            return in;
        }

        if (!(where.getValue() instanceof Hefesto<?> subQuery)) {
            throw new UnsupportedOperationException("Invalid value for Where IN operation");
        }

        if (subQuery.getSelects().size() != 1) {
            throw new QueryException("The quantity of select for sub-query must be 1 for Where IN operation");
        }

        if (!subQuery.hasCustomResultForSubQuery()) {
            throw new QueryException("The sub-query must have custom result for Where IN operation");
        }

        in.value(subQuery.getSubQuery(cr, root, cb, joins));

        return in;
    }

    private Predicate applyWhereOperation(BaseWhere where, Predicate... predicate) {
        switch (where.getWhereOperation()) {
            case OR -> {
                return cb.or(predicate);
            }
            case AND -> {
                return cb.and(predicate);
            }
            default ->
                    throw new UnsupportedOperationException("Unsupported where operation: " + where.getWhereOperation());
        }
    }

    private Number getTransformedValue(Object originalValue, Path<Number> path) {
        Class<? extends Number> typeField = path.getJavaType();
        return (Number) CastUtils.castValue(typeField, originalValue);
    }

    public void constructSubQuery(Subquery<?> subQuery, CriteriaBuilder cb, Root<?> root, Root<?> parentRoot) {
        if (isEmpty()) {
            return;
        }

        this.cb = cb;
        this.root = root;
        this.parentRoot = parentRoot;

        subQuery.where(transform(items));
    }
}
