package io.github.robertomike.hefesto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum JoinOperator {
    INNER("inner"),
    RIGHT("right"),
    LEFT("left");

    final String operator;
}
