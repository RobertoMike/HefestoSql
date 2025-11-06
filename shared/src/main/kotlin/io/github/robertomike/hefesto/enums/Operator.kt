package io.github.robertomike.hefesto.enums

enum class Operator(val operator: String) {
    EQUAL("="),
    DIFF("<>"),
    LESS_OR_EQUAL("<="),
    LESS("<"),
    GREATER_OR_EQUAL(">="),
    GREATER(">"),
    IN("in"),
    LIKE("like"),
    NOT_LIKE("not like"),
    NOT_IN("not in"),
    IS_NULL("is null"),
    IS_NOT_NULL("is not null"),
    FIND_IN_SET("find_in_set"),
    NOT_FIND_IN_SET("find_in_set")
}
