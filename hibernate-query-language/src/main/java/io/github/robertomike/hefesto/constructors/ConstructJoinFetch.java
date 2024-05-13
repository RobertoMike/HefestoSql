package io.github.robertomike.hefesto.constructors;

import io.github.robertomike.hefesto.actions.JoinFetch;
import io.github.robertomike.hefesto.builders.Hefesto;

public class ConstructJoinFetch extends Construct<JoinFetch> {
    public String construct(Hefesto<?> builder) {
        StringBuilder joinQuery = new StringBuilder();

        items.forEach((value) -> {
            if (!joinQuery.isEmpty()) {
                joinQuery.append(" ");
            }

            joinQuery.append(apply(builder, value));
        });

        return joinQuery.toString();
    }

    public String apply(Hefesto<?> builder, JoinFetch join) {
        return join.getJoinType() + " join fetch " +
                (join.isNested() ? "" : builder.getAcronymTable() + ".") + join.getRelationship() +
                (join.getAlias() != null ? " as " + join.getAlias() : "");
    }
}
