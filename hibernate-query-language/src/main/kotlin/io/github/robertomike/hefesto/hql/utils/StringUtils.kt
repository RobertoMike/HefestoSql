package io.github.robertomike.hefesto.hql.utils

/**
 * Utility object for string manipulation operations in HQL query construction.
 * Provides helper methods for string splitting and field path parsing.
 */
object StringUtils {
    /** Constant for empty string */
    const val EMPTY = ""
    
    /** Constant for empty string array */
    val EMPTY_ARRAY = arrayOf<String>()

    /**
     * Splits a string by dot (.) delimiter.
     * Used for parsing nested field paths like "user.address.city".
     *
     * @param value the string to split
     * @return array of string parts, or empty array if input is empty
     */
    @JvmStatic
    fun splitByDot(value: String): Array<String> {
        return split(value, "\\.")
    }

    /**
     * Splits a string by a custom regular expression pattern.
     *
     * @param value the string to split
     * @param regExpression the regular expression to split by
     * @return array of string parts, or empty array if input is empty
     */
    @JvmStatic
    fun split(value: String, regExpression: String): Array<String> {
        val result = value.trim()
        return if (result.isEmpty()) EMPTY_ARRAY else result.split(regExpression.toRegex()).toTypedArray()
    }
}
