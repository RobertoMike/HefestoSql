package io.github.robertomike.hefesto.utils

import io.github.robertomike.hefesto.exceptions.HefestoException

object ClassUtils {
    /**
     * Create a new object of a class.
     *
     * @param classToInstantiate a class of an object
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
