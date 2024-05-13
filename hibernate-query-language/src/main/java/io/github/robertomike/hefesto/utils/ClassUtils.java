package io.github.robertomike.hefesto.utils;

import io.github.robertomike.hefesto.exceptions.HefestoException;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ClassUtils {
    /**
     * Create a new object of a class.
     *
     * @param classToInstantiate a class of an object
     */
    public static <T> T newInstance(Class<T> classToInstantiate) {
        try {
            return classToInstantiate.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            throw new HefestoException(String.format("Could not instantiate a class: %s",
                    classToInstantiate.getName()), ex);
        }
    }
}
