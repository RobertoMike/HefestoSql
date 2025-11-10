package io.github.robertomike.hefesto.enums

/**
 * Enumeration of aggregate functions that can be used in SELECT clauses.
 * 
 * Example usage:
 * ```java
 * // Count records
 * .addSelect("id", SelectOperator.COUNT)
 * 
 * // Sum values
 * .addSelect("price", SelectOperator.SUM, "totalPrice")
 * 
 * // Average
 * .addSelect("age", SelectOperator.AVG, "averageAge")
 * 
 * // Min/Max
 * .addSelect("salary", SelectOperator.MAX, "maxSalary")
 * ```
 */
enum class SelectOperator(val function: String) {
    /** COUNT aggregate function - counts non-null values */
    COUNT("count(%s)"),
    
    /** AVG aggregate function - calculates average of numeric values */
    AVG("avg(%s)"),
    
    /** MIN aggregate function - finds minimum value (works with Comparable types) */
    MIN("min(%s)"),
    
    /** MAX aggregate function - finds maximum value (works with Comparable types) */
    MAX("max(%s)"),
    
    /** SUM aggregate function - sums numeric values */
    SUM("sum(%s)")
}
