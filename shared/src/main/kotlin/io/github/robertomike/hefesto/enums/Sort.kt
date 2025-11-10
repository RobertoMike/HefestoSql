package io.github.robertomike.hefesto.enums

/**
 * Enum representing sort order directions for ORDER BY clauses.
 */
enum class Sort(val sort: String) {
    /** Ascending order (smallest to largest, A to Z) */
    ASC("asc"),
    
    /** Descending order (largest to smallest, Z to A) */
    DESC("desc")
}
