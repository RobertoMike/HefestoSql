package io.github.robertomike.enums;

public enum WhereOperator {
    OR("or"),
    AND("and");

    public final String operator;

    WhereOperator(String operator) {
        this.operator = operator;
    }
}
