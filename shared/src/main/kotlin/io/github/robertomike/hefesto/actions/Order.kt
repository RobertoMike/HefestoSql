package io.github.robertomike.hefesto.actions

import io.github.robertomike.hefesto.enums.Sort

data class Order(
    val field: String,
    val sort: Sort = Sort.ASC
)
