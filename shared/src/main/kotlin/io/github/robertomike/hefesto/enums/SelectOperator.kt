package io.github.robertomike.hefesto.enums

enum class SelectOperator(val function: String) {
    COUNT("count(%s)"),
    AVG("avg(%s)"),
    MIN("min(%s)"),
    MAX("max(%s)"),
    SUM("sum(%s)")
}
