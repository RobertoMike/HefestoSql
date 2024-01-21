package io.github.robertomike.hefesto.utils;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;

public class HibernateUtils {
    public static final String DOT_REGEX = "\\.";

    public static <T> Path<T> getFieldFrom(From<?, ?> from, String field) {
        if (!field.contains(".")) {
            return from.get(field);
        }

        var split = field.split(DOT_REGEX);

        Path<T> path = null;

        for (var o : split) {
            if (path == null) {
                path = from.get(o);
                continue;
            }
            path = path.get(o);
        }

        return path;
    }
}
