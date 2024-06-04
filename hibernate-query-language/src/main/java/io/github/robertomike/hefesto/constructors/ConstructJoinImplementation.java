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

        var basic = join.getJoinOperator().getOperator() + " join " + table + " " + join.getAcronym();

        if (join.getFieldReference() != null && join.getFieldJoin() != null) {
            var fieldReference = join.getFieldReference();

            fieldReference = fieldReference.contains(".") ? fieldReference : fatherAcronym + "." + fieldReference;

            return join.getJoinOperator().getOperator() + " join " + join.getTable() + " " + join.getAcronym()
                    + " on " + join.getAcronym() + "." + join.getFieldJoin() + " = " + fieldReference;
        }

        return basic;
    }
}
