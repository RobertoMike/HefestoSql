package io.github.robertomike.hefesto.enums

/**
 * Enumeration of SQL join types.
 * Defines the type of join operation to perform between entities.
 * 
 * Example usage:
 * ```java
 * // INNER join - only matching rows
 * .join("posts", JoinOperator.INNER)
 * 
 * // LEFT join - all left rows + matching right rows
 * .join("posts", JoinOperator.LEFT)
 * 
 * // RIGHT join - all right rows + matching left rows
 * .join("posts", JoinOperator.RIGHT)
 * ```
 */
enum class JoinOperator(val operator: String) {
    /** INNER JOIN - returns only rows with matches in both tables */
    INNER("inner"),
    
    /** RIGHT JOIN - returns all rows from right table and matched rows from left table */
    RIGHT("right"),
    
    /** LEFT JOIN - returns all rows from left table and matched rows from right table */
    LEFT("left")
}
