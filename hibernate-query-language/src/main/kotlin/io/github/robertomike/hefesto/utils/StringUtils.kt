package io.github.robertomike.hefesto.utils

object StringUtils {
    const val EMPTY = ""
    val EMPTY_ARRAY = arrayOf<String>()

    @JvmStatic
    fun splitByDot(value: String): Array<String> {
        return split(value, "\\.")
    }

    @JvmStatic
    fun split(value: String, regExpression: String): Array<String> {
        val result = value.trim()
        return if (result.isEmpty()) EMPTY_ARRAY else result.split(regExpression.toRegex()).toTypedArray()
    }
}
