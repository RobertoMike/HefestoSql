package io.github.robertomike.hefesto.hql.utils

import io.github.robertomike.hefesto.exceptions.HefestoException
import org.hibernate.query.TupleTransformer
import java.lang.reflect.Constructor

class FluentHibernateResultTransformer<T>(
    private val resultClass: Class<*>
) : TupleTransformer<T> {

    private var setters: Array<NestedSetter>? = null
    private var constructors: Array<Constructor<*>>? = null

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
