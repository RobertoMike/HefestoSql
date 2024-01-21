package io.github.robertomike.hefesto.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum SelectOperator {
    COUNT("count(%s)"),
    AVG("avg(%s)"),
    MIN("min(%s)"),
    MAX("max(%s)"),
    SUM("sum(%s)");

    public final String function;
}
