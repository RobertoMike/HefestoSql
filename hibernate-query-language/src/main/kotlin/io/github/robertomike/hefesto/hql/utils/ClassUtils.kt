package io.github.robertomike.hefesto.hql.utils

import io.github.robertomike.hefesto.exceptions.HefestoException

/**
 * Utility object for class instantiation and reflection operations.
 * Provides safe methods for creating instances of classes dynamically.
 */
object ClassUtils {
    /**
     * Creates a new instance of the specified class using its no-argument constructor.
     * 
     * This method is used internally for DTO instantiation during query result mapping.
     *
     * @param classToInstantiate the class to instantiate
     * @param <T> the type of the class
     * @return a new instance of the specified class
     * @throws HefestoException if the class cannot be instantiated (e.g., no default constructor)
     */
    @JvmStatic
    fun <T> newInstance(classToInstantiate: Class<T>): T {
        return try {
            classToInstantiate.getDeclaredConstructor().newInstance()
        } catch (ex: Exception) {
            throw HefestoException("Could not instantiate a class: ${classToInstantiate.name}", ex)
        }
    }
}
