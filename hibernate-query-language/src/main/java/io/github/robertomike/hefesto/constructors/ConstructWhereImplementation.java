package io.github.robertomike.hefesto.constructors;

import io.github.robertomike.hefesto.actions.wheres.*;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.exceptions.QueryException;
import io.github.robertomike.hefesto.exceptions.UnsupportedOperationException;

import java.util.*;

public class ConstructWhereImplementation extends ConstructWhere {
    private Map<String, Object> params;

    public String construct(Map<String, Object> params) {
        this.params = params;

        if (items.isEmpty()) {
            return "";
        }
        List<String> wheresQuery = new ArrayList<>();

        iterator(wheresQuery, items);

        if (!wheresQuery.isEmpty()) {
            wheresQuery.add(0, "Where ");
        }

        return String.join("", wheresQuery);
    }


    public void iterator(List<String> wheresQuery, List<? extends BaseWhere> whereList) {
        whereList.forEach((value) -> {
            if (value instanceof CollectionWhere) {
                if (!wheresQuery.isEmpty()) {
                    wheresQuery.add(" " + value.getWhereOperation().operator);
                }

                wheresQuery.add(" (");
                iterator(wheresQuery, ((CollectionWhere) value).getWheres());
                wheresQuery.add(")");
                return;
            }
            constructBaseWhere(wheresQuery, value);
        });
    }

    public void constructBaseWhere(List<String> wheresQuery, BaseWhere where) {
        if (!wheresQuery.isEmpty()) {
            if (!wheresQuery.get(wheresQuery.size() - 1).equals(" ("))
                wheresQuery.add(" " + where.getWhereOperation().operator);
        }

        wheresQuery.add(" ");

        if (where instanceof WhereRaw whereRaw) {
            wheresQuery.add(whereRaw.query);
            return;
        }

        if (where instanceof WhereExist whereExist) {
            wheresQuery.add(whereExist.isExists() ? "exists" : "not exists");
            wheresQuery.add("(");
            wheresQuery.add(((Hefesto<?>) whereExist.getSubQuery()).getSubQuery(params));
            wheresQuery.add(")");
            return;
        }

        if (where instanceof WhereField whereField) {
            constructWhereField(wheresQuery, whereField);
            return;
        }

        if (where instanceof Where nativeWhere) {
            constructWhere(wheresQuery, nativeWhere);
            return;
        }

        throw new QueryException("Invalid class extended from BaseWhere: " + where.getClass());
    }

    private void constructWhereField(List<String> wheresQuery, WhereField whereField) {
        switch (whereField.getOperator()) {
            case LIKE, NOT_LIKE, EQUAL, DIFF, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL ->
                    wheresQuery.add(whereField.getParentField() + " " + whereField.getOperator().operator + " " + whereField.getField());

            default -> throw new UnsupportedOperationException("Unsupported operator: " + whereField.getOperator());
        }
    }

    public String standardizeNameParam(String field, int size) {
        return field.replace(".", "")
                .replace("(", "_")
                .replace(")", "_") + "_" + size;
    }

    public String standardizeNameParamWhere(String nameParam) {
        return ":" + nameParam;
    }

    public void constructWhere(List<String> wheresQuery, Where where) {
        String field = where.getField();
        String nameParam = standardizeNameParam(field, wheresQuery.size());
        String nameParamWhere = standardizeNameParamWhere(nameParam);
        String operator = where.getOperator().operator;
        var value = where.getValue();

        boolean param = true;

        switch (where.getOperator()) {
            case IN, NOT_IN -> {
                if (where.getValue() instanceof Hefesto<?> builder) {
                    wheresQuery.add(field + " " + operator + " (" + builder.getSubQuery(params) + ")");
                    return;
                }

                wheresQuery.add(field + " " + operator + " (" + nameParamWhere + ")");

                Object values;
                if (value.getClass().isArray()) {
                    values = Arrays.asList((Object[]) value);
                } else if (value instanceof Collection<?> collection) {
                    values = new ArrayList<>(collection);
                } else if (where.getValue() instanceof Hefesto<?> builder) {
                    values = builder.getSubQuery(params);
                } else {
                    throw new UnsupportedOperationException("Invalid class: " + value.getClass());
                }

                params.put(nameParam, values);
                param = false;
            }

            case IS_NULL, IS_NOT_NULL -> {
                wheresQuery.add(field + " " + operator);
                param = false;
            }

            case NOT_FIND_IN_SET ->
                wheresQuery.add(operator + "(" + nameParamWhere + "," + field + ") = 0");
            case FIND_IN_SET ->
                wheresQuery.add(operator + "(" + nameParamWhere + "," + field + ") > 0");

            default -> wheresQuery.add(field + " " + operator + " " + nameParamWhere);
        }

        if (param) {
            params.put(nameParam, value);
        }
    }
}
