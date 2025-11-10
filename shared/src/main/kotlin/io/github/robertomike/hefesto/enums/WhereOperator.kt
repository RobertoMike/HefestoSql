package io.github.robertomike.hefesto.enums

/**
 * Enumeration of logical operators used to combine WHERE conditions.
 * 
 * Example usage:
 * ```java
 * // AND operator (default) - all conditions must be true
 * .where("age", Operator.GREATER, 18)
 * .where("status", "ACTIVE")  // implicitly AND
 * 
 * // OR operator - at least one condition must be true
 * .whereAny(group -> {
 *     group.where("name", "John");
 *     group.where("email", "john@mail.com");
 * })
 * ```
 */
enum class WhereOperator(val operator: String) {
    /** OR logical operator - at least one condition must be true */
    OR("or"),
    
    /** AND logical operator - all conditions must be true */
    AND("and")
}
