package io.github.robertomike.hefesto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This class define all the supported where operators
 */
@AllArgsConstructor
@Getter
public enum WhereOperator {
    OR("or"),
    AND("and");

    public final String operator;
}
