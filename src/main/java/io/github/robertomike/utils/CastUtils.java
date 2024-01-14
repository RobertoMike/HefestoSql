package io.github.robertomike.utils;

import io.github.robertomike.exceptions.QueryException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CastUtils {
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
}
