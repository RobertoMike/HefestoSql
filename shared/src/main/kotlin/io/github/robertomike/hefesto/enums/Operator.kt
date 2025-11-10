package io.github.robertomike.hefesto.enums

/**
 * Enumeration of comparison operators used in WHERE clauses.
 * These operators work with any Comparable type including numbers, strings, and dates.
 * 
 * Example usage:
 * ```java
 * // Numbers
 * .where("age", Operator.GREATER, 18)
 * 
 * // Strings
 * .where("name", Operator.LIKE, "John%")
 * 
 * // Dates
 * .where("createdAt", Operator.LESS, LocalDateTime.now())
 * 
 * // Collections
 * .where("status", Operator.IN, List.of("ACTIVE", "PENDING"))
 * ```
 */
enum class Operator(val operator: String) {
    /** Equality comparison (=) */
    EQUAL("="),
    
    /** Difference/inequality comparison (<>) */
    DIFF("<>"),
    
    /** Less than or equal comparison (<=) - works with Comparable types */
    LESS_OR_EQUAL("<="),
    
    /** Less than comparison (<) - works with Comparable types */
    LESS("<"),
    
    /** Greater than or equal comparison (>=) - works with Comparable types */
    GREATER_OR_EQUAL(">="),
    
    /** Greater than comparison (>) - works with Comparable types */
    GREATER(">"),
    
    /** IN clause for checking if value is in a collection */
    IN("in"),
    
    /** LIKE pattern matching for strings */
    LIKE("like"),
    
    /** NOT LIKE pattern matching for strings */
    NOT_LIKE("not like"),
    
    /** NOT IN clause for checking if value is not in a collection */
    NOT_IN("not in"),
    
    /** IS NULL check */
    IS_NULL("is null"),
    
    /** IS NOT NULL check */
    IS_NOT_NULL("is not null"),
    
    /**
     * FIND_IN_SET for checking if value exists in a comma-separated string or Set field.
     * Uses MySQL's FIND_IN_SET function: `FIND_IN_SET(value, field) > 0`
     * 
     * Example:
     * ```java
     * // Check if ACTIVE status exists in the status set
     * .where("status", Operator.FIND_IN_SET, Status.ACTIVE)
     * ```
     */
    FIND_IN_SET("find_in_set"),
    
    /**
     * NOT FIND_IN_SET for checking if value does NOT exist in a comma-separated string or Set field.
     * Uses MySQL's FIND_IN_SET function with negation: `FIND_IN_SET(value, field) = 0`
     * 
     * Note: Both FIND_IN_SET and NOT_FIND_IN_SET use the same underlying function,
     * but differ in how the result is evaluated (> 0 vs = 0).
     * 
     * Example:
     * ```java
     * // Check if ACTIVE status does NOT exist in the status set
     * .where("status", Operator.NOT_FIND_IN_SET, Status.ACTIVE)
     * ```
     */
    NOT_FIND_IN_SET("find_in_set")
}
