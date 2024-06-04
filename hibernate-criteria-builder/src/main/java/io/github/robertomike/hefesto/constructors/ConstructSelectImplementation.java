package io.github.robertomike.hefesto.constructors;

import io.github.robertomike.hefesto.actions.Select;
import io.github.robertomike.hefesto.enums.SelectOperator;
import io.github.robertomike.hefesto.exceptions.QueryException;
import io.github.robertomike.hefesto.utils.HibernateUtils;
import io.github.robertomike.hefesto.models.BaseModel;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConstructSelectImplementation<T extends BaseModel> extends ConstructSelect {
    private Map<String, Join<?, ?>> joins;
    private CriteriaBuilder cb;

    public ConstructSelectImplementation<T> setJoins(Map<String, Join<?, ?>> joins) {
        this.joins = joins;
        return this;
    }

    public void construct(Root<T> root, CriteriaQuery<T> cr, CriteriaBuilder cb) {
        if (isEmpty()) {
            cr.select(root);
            return;
        }

        multiSelect(root, cr, cb);
    }

    public void multiSelect(Root<?> root, CriteriaQuery<?> cr, CriteriaBuilder cb) {
        this.cb = cb;

        List<Selection<?>> selects = new ArrayList<>();

        items.forEach(element -> {
            Selection<?> select = getSelectField(root, element);


            if (element.getAlias() != null) {
                select = select.alias(element.getAlias());
            }

            selects.add(select);
        });

        cr.multiselect(selects.toArray(new Selection[]{}));
    }

    private Expression<?> getSelectField(Root<?> root, Select element) {
        From<?, ?> from = root;
        var field = element.getField();

        if (element.getField().contains(".") && joins.containsKey(field.split(HibernateUtils.DOT_REGEX)[0])) {
            var splitted = field.split(HibernateUtils.DOT_REGEX);
            from = joins.get(splitted[0]);
            field = splitted[1];
        }

        if (field.contains("*")) {
            return from;
        }

        Expression<?> select = HibernateUtils.getFieldFrom(from, field);

        if (element.getOperator() != null) {
            select = getWithFunction(select, element.getOperator());
        }

        return select;
    }

    @SuppressWarnings("unchecked")
    private Expression<?> getWithFunction(Expression<?> select, SelectOperator operator) {
        return switch (operator) {
            case SUM -> cb.sum((Expression<Number>) select);
            case AVG -> cb.avg((Expression<Number>) select);
            case MAX -> cb.max((Expression<Number>) select);
            case MIN -> cb.min((Expression<Number>) select);
            case COUNT -> cb.count(select);
        };
    }

    @SuppressWarnings("unchecked")
    public void constructSubQuery(Root<?> root, Subquery<?> sub) {
        if (isEmpty()) {
            ((Subquery<Object>) sub).select((Expression<Object>) root);
            return;
        }

        if (size() != 1) {
            throw new QueryException("Sub-query must have only one select when is using Where IN operation");
        }

        ((Subquery<Object>) sub).select((Expression<Object>) getSelectField(root, items.get(0)));
    }
}
