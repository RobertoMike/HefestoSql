package io.github.robertomike.hefesto.utils

data class Page<T>(
    val data: List<T>,
    val page: Long,
    val total: Long
)
