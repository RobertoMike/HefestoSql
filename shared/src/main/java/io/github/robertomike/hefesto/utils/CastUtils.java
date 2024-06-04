package io.github.robertomike.hefesto.utils;

import io.github.robertomike.hefesto.exceptions.HefestoException;
import io.github.robertomike.hefesto.exceptions.QueryException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This class is a helper to cast values
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CastUtils {
    /**
     * Casts a value to the specified type.
     *
     * @param  type   the target class type for the casting
     * @param  value  the value to be casted
     * @param <T>     the type of the value
     * @return        the casted value of the specified type
     */
    public static <T> Object castValue(Class<T> type, Object value) {
        if (type.isInstance(value)) {
            return value;
        }

        if (type.equals(String.class)) {
            return type.toString();
        }
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return Integer.valueOf(value.toString());
        }
        if (type.equals(Byte.class) || type.equals(byte.class)) {
            return Integer.valueOf(value.toString());
        }
        if (type.equals(Double.class) || type.equals(double.class)) {
            return Double.valueOf(value.toString());
        }
        if (type.equals(Short.class) || type.equals(short.class)) {
            return Short.valueOf(value.toString());
        }
        if (type.equals(Long.class) || type.equals(long.class)) {
            return Long.valueOf(value.toString());
        }
        if (type.equals(BigDecimal.class)) {
            return BigDecimal.valueOf(Double.parseDouble(value.toString()));
        }
        if (type.equals(BigInteger.class)) {
            return BigInteger.valueOf(Long.parseLong(value.toString()));
        }
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return Boolean.valueOf(value.toString());
        }
        if (type.equals(char.class)) {
            return value.toString().charAt(0);
        }

        throw new QueryException("Unsupported casting type:" + type);
    }

    public static <T> T getClassInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new HefestoException("Error creating class instance for " + clazz.getName() , e);
        }
    }
}
