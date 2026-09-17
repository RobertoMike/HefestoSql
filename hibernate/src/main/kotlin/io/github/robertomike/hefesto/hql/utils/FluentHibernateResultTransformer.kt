package io.github.robertomike.hefesto.hql.utils

import io.github.robertomike.hefesto.exceptions.HefestoException
import org.hibernate.query.TupleTransformer
import java.lang.reflect.Constructor

/**
 * Hibernate result transformer that maps query results to custom DTO classes.
 * 
 * This transformer intelligently handles result mapping by trying multiple strategies:
 * 1. Direct type match - if result is already the target type
 * 2. Constructor injection - if a matching constructor exists
 * 3. Setter injection - using JavaBean setters with support for nested properties
 * 
 * The transformer caches reflection metadata for performance. Nested properties
 * are supported using dot notation (e.g., "user.address.city").
 * 
 * Example usage (internal):
 * ```kotlin
 * // Maps Object[] results to UserDTO
 * val transformer = FluentHibernateResultTransformer<UserDTO>(UserDTO::class.java)
 * query.setTupleTransformer(transformer)
 * ```
 *
 * @param T the target result type
 * @param resultClass the class to transform results into
 */
class FluentHibernateResultTransformer<T>(
    private val resultClass: Class<*>
) : TupleTransformer<T> {

    private var setters: Array<NestedSetter>? = null
    private var constructors: Array<Constructor<*>>? = null

    /**
     * Transforms a tuple (array of values) and aliases into a result object.
     * 
     * Strategy:
     * 1. If tuple contains target type, return it directly
     * 2. Try to find matching constructor and invoke it
     * 3. Fall back to setter injection with nested property support
     *
     * @param tuple the array of values from the query result
     * @param aliases the column/field aliases from the query
     * @return the transformed result object
     */
    override fun transformTuple(tuple: Array<Any?>, aliases: Array<String>): T {
        if (tuple.isNotEmpty() && tuple[0] != null && tuple[0]!!.javaClass == resultClass) {
            @Suppress("UNCHECKED_CAST")
            return tuple[0] as T
        }

        createCachedConstructors(resultClass, tuple.size)

        if (validConstructorForTuple(constructors!!, tuple)) {
            @Suppress("UNCHECKED_CAST")
            return createObject(constructors!!, tuple) as T
        }

        val processedAliases = aliases.map { it.replace("_", ".") }.toTypedArray()

        createCachedSetters(resultClass, processedAliases)

        val result = ClassUtils.newInstance(resultClass)

        for (i in processedAliases.indices) {
            setters!![i].set(result, tuple[i])
        }

        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    private fun createObject(constructors: Array<Constructor<*>>, tuple: Array<Any?>): Any {
        val constructor = getConstructorForTuple(constructors, tuple)
        return try {
            constructor!!.newInstance(*tuple)
        } catch (e: Exception) {
            throw HefestoException("Problem while creating object with constructor", e)
        }
    }

    private fun validConstructorForTuple(constructors: Array<Constructor<*>>, tuple: Array<Any?>): Boolean {
        return getConstructorForTuple(constructors, tuple) != null
    }

    private fun getConstructorForTuple(constructors: Array<Constructor<*>>, tuple: Array<Any?>): Constructor<*>? {
        for (constructor in constructors) {
            val parameterTypes = constructor.parameterTypes

            var valid = true
            for (i in parameterTypes.indices) {
                if (tuple[i] == null || parameterTypes[i] != tuple[i]!!.javaClass) {
                    valid = false
                    break
                }
            }

            if (valid) {
                return constructor
            }
        }
        return null
    }

    private fun createCachedConstructors(resultClass: Class<*>, length: Int) {
        if (constructors == null) {
            constructors = resultClass.constructors
                .filter { it.parameterCount == length }
                .toTypedArray()
        }
    }

    private fun createCachedSetters(resultClass: Class<*>, aliases: Array<String>) {
        if (setters == null) {
            setters = createSetters(resultClass, aliases)
        }
    }

    companion object {
        private fun createSetters(resultClass: Class<*>, aliases: Array<String>): Array<NestedSetter> {
            return Array(aliases.size) { i ->
                NestedSetter.create(resultClass, aliases[i])
            }
        }
    }
}
