package io.github.robertomike.hefesto.constructors;

import io.github.robertomike.hefesto.actions.JoinFetch;
import jakarta.persistence.criteria.Root;

public class ConstructJoinFetch extends Construct<JoinFetch> {
    public void construct(Root<?> root) {
        items.forEach(value -> root.fetch(value.getRelationship(), value.getJoinType()));
    }
}
