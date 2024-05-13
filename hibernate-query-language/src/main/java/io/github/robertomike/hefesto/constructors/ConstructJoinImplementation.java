package io.github.robertomike.hefesto.constructors;

import io.github.robertomike.hefesto.actions.Join;
import io.github.robertomike.hefesto.builders.Hefesto;
import lombok.Getter;

@Getter
public class ConstructJoinImplementation extends ConstructJoin {
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

    public String apply(Hefesto<?> builder, Join join) {
        String fatherAcronym = builder.getAcronymTable();
        String table = join.getTable();

        if (!table.contains(".")) {
            table = fatherAcronym + "." + table;
        }

        return join.getJoinOperator().getOperator() + " join " + table + " " + join.getAcronym();
    }
}
