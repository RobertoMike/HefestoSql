package io.github.robertomike.hefesto.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum JoinOperator {
    INNER("inner"),
    RIGHT("right"),
    LEFT("left");

    final String operator;
}
