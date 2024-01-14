package io.github.robertomike.constructors;

import io.github.robertomike.actions.JoinFetch;
import jakarta.persistence.criteria.Root;

public class ConstructJoinFetch extends Construct<JoinFetch> {
    public void construct(Root<?> root) {
        items.forEach(value -> root.fetch(value.getRelationship(), value.getJoinType()));
    }
}
