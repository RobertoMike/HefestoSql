package io.github.robertomike.hefesto.constructors;

import lombok.Getter;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

@Getter
public class ConstructGroupByImplementation extends ConstructGroupBy {
    public void construct(CriteriaQuery<?> cr, Root<?> root) {
        if (items.isEmpty()) {
            return;
        }

        cr.groupBy(items.stream().map(value -> root.get(value.field())).toArray(Expression[]::new));
    }
}
