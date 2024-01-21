package io.github.robertomike.hefesto.constructors;

import io.github.robertomike.hefesto.utils.HibernateUtils;
import jakarta.persistence.criteria.*;
import org.hibernate.QueryException;

import java.util.Map;

public class ConstructOrderImplementation extends ConstructOrder {
    private CriteriaBuilder cb;
    private Root<?> root;
    private Map<String, Join<?, ?>> joins;

    public ConstructOrderImplementation setJoins(Map<String, Join<?, ?>> joins) {
        this.joins = joins;
        return this;
    }

    public void construct(CriteriaBuilder cb, CriteriaQuery<?> cr, Root<?> root) {
        this.cb = cb;
        this.root = root;

        cr.orderBy(items.stream().map(this::constructOrder).toList());
    }

    private Order constructOrder(io.github.robertomike.hefesto.actions.Order value) {
        From<?, ?> from = root;
        var field = value.getField();

        if (field.contains(".") && joins.containsKey(field.split(HibernateUtils.DOT_REGEX)[0])) {
            var splitted = field.split(HibernateUtils.DOT_REGEX);
            from = joins.get(splitted[0]);
            field = splitted[1];
        }


        switch (value.getSort()) {
            case ASC -> {
                return cb.asc(from.get(field));
            }
            case DESC -> {
                return cb.desc(from.get(field));
            }
            default -> throw new QueryException("Unsupported sort: " + value.getSort());
        }
    }
}
