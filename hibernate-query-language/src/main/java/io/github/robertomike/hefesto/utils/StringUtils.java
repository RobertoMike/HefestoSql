package io.github.robertomike.hefesto.utils;

public final class StringUtils {

    public static final String EMPTY = "";

    public static final String[] EMPTY_ARRAY = new String[0];


    public static String[] splitByDot(String value) {
        return split(value, "\\.");
    }

    public static String[] split(String value, String regExpression) {
        String result = value == null ? EMPTY : value.trim();
        return result.isEmpty() ? EMPTY_ARRAY : result.split(regExpression);
    }
}
