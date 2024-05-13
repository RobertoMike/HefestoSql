package io.github.robertomike.hefesto.constructors;

import io.github.robertomike.hefesto.actions.Select;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.SelectOperator;
import io.github.robertomike.hefesto.exceptions.QueryException;

public class ConstructSelectImplementation extends ConstructSelect {
    private final String prefix = "select ";
    private Hefesto<?> hefesto;
    private boolean nested = false;

    public String construct(Hefesto<?> builder) {
        this.hefesto = builder;
        var select = items.stream().map(this::getSelectField).reduce((a, b) -> a + ", " + b).orElse(null);

        if (select == null) {
            return "";
        }

        return prefix + select;
    }

    private String getSelectField(Select select) {
        var field = select.getField();

        if (!field.contains(".")) {
            field = hefesto.getAcronymTable() + "." + field;
        }

        if (select.getOperator() != null) {
            return addAlias(getWithFunction(field, select.getOperator()), select);
        }

        return addAlias(field, select);
    }

    private String getWithFunction(String field, SelectOperator operator) {
        return switch (operator) {
            case SUM -> "sum(" + field + ")";
            case AVG -> "avg(" + field + ")";
            case MAX -> "max(" + field + ")";
            case MIN -> "min(" + field + ")";
            case COUNT -> "count(" + field + ")";
        };
    }

    private String addAlias(String function, Select select) {
        if (select.getAlias() != null) {
            return function + " as " + select.getAlias().replace(".", "_");
        }

        return nested ? function : function + " as " + select.getField().replace(".", "_");
    }

    public String constructSubQuery(Hefesto<?> builder) {
        nested = true;

        if (isEmpty()) {
            return prefix + builder.getAcronymTable();
        }

        if (size() != 1) {
            throw new QueryException("Sub-query must have only one select when is using Where IN operation");
        }

        return prefix + getSelectField(items.get(0));
    }
}
