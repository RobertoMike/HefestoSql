package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.exceptions.HefestoException
import io.github.robertomike.hefesto.exceptions.QueryException
import java.math.BigDecimal
import java.math.BigInteger

/**
 * This class is a helper to cast values
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

    @JvmStatic
    fun <T> getClassInstance(clazz: Class<T>): T {
        return try {
            clazz.getConstructor().newInstance()
        } catch (e: Exception) {
            throw HefestoException("Error creating class instance for ${clazz.name}", e)
        }
    }
}
