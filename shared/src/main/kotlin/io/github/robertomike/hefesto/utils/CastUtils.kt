package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.exceptions.HefestoException
import io.github.robertomike.hefesto.exceptions.QueryException
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Utility object for type casting and class instantiation operations.
 * Provides safe casting methods for common data types and dynamic object creation.
 * 
 * This utility handles conversions between various numeric types, strings, booleans,
 * and other common Java types used in query results.
 */
object CastUtils {
    /**
     * Casts a value to the specified type.
     *
     * @param  type   the target class type for the casting
     * @param  value  the value to be casted
     * @param <T>     the type of the value
     * @return        the casted value of the specified type
     */
    @JvmStatic
    fun <T> castValue(type: Class<T>, value: Any): Any {
        if (type.isInstance(value)) {
            return value
        }

        if (type == String::class.java || type == java.lang.String::class.java) {
            return value.toString()
        }
        if (type == Int::class.javaPrimitiveType || type == Integer::class.java || type == java.lang.Integer::class.java) {
            return value.toString().toInt()
        }
        if (type == Byte::class.javaPrimitiveType || type == java.lang.Byte::class.java) {
            return value.toString().toByte()
        }
        if (type == Double::class.javaPrimitiveType || type == java.lang.Double::class.java) {
            return value.toString().toDouble()
        }
        if (type == Short::class.javaPrimitiveType || type == java.lang.Short::class.java) {
            return value.toString().toShort()
        }
        if (type == Long::class.javaPrimitiveType || type == java.lang.Long::class.java) {
            return value.toString().toLong()
        }
        if (type == BigDecimal::class.java) {
            return BigDecimal.valueOf(value.toString().toDouble())
        }
        if (type == BigInteger::class.java) {
            return BigInteger.valueOf(value.toString().toLong())
        }
        if (type == Boolean::class.javaPrimitiveType || type == java.lang.Boolean::class.java) {
            return value.toString().toBoolean()
        }
        if (type == Char::class.javaPrimitiveType || type == Character::class.java) {
            return value.toString()[0]
        }

        throw QueryException("Unsupported casting type: $type")
    }

    /**
     * Creates a new instance of the specified class using its no-argument constructor.
     * 
     * This method is typically used internally for DTO instantiation during query result mapping.
     *
     * @param clazz the class to instantiate
     * @param <T> the type of the class
     * @return a new instance of the specified class
     * @throws HefestoException if the class cannot be instantiated (e.g., no default constructor)
     */
    @JvmStatic
    fun <T> getClassInstance(clazz: Class<T>): T {
        return try {
            clazz.getConstructor().newInstance()
        } catch (e: Exception) {
            throw HefestoException("Error creating class instance for ${clazz.name}", e)
        }
    }
}
