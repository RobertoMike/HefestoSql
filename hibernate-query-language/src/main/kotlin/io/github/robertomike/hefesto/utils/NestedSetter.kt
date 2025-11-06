package io.github.robertomike.hefesto.utils

import org.hibernate.PropertyAccessException
import java.lang.reflect.Method

class NestedSetter private constructor(
    private val clazz: Class<*>,
    private val getMethods: Array<Method>,
    private val setMethods: Array<Method>,
    private val method: Method,
    private val propertyName: String
) {

    fun set(target: Any, value: Any?) {
        try {
            invokeSet(target, value)
        } catch (ex: Exception) {
            checkForPrimitive(value)
            val errorMessage = String.format(
                "Setter information: expected type: %s, actual type: %s.",
                method.parameterTypes[0].name,
                value?.javaClass?.name
            )
            throw PropertyAccessException(ex, errorMessage, true, clazz, propertyName)
        }
    }

    private fun checkForPrimitive(value: Any?) {
        if (value == null && method.parameterTypes[0].isPrimitive) {
            throw PropertyAccessException(
                null,
                "Value is null, but property type is primitive.", true, clazz, propertyName
            )
        }
    }

    private fun invokeSet(target: Any, value: Any?) {
        var tmpTarget = target
        for (i in getMethods.indices) {
            var tmpTarget2 = getMethods[i].invoke(tmpTarget)
            if (tmpTarget2 == null) {
                tmpTarget2 = ClassUtils.newInstance(getMethods[i].returnType)
                setMethods[i].invoke(tmpTarget, tmpTarget2)
            }
            tmpTarget = tmpTarget2
        }
        method.invoke(tmpTarget, value)
    }

    companion object {
        /**
         * Create a setter for a nested property.
         */
        @JvmStatic
        fun create(theClass: Class<*>, propertyName: String): NestedSetter {
            return getSetterOrNull(theClass, propertyName)
                ?: throw PropertyAccessException(
                    null,
                    "Could not find a setter for a nested property.",
                    true,
                    theClass,
                    propertyName
                )
        }

        private fun getSetterOrNull(theClass: Class<*>?, propertyName: String?): NestedSetter? {
            if (theClass == Any::class.java || theClass == null || propertyName == null) {
                return null
            }

            val propertyParts = ReflectionUtils.getPropertyParts(propertyName)
            val nestedCount = propertyParts.size

            val getMethods = arrayOfNulls<Method>(nestedCount - 1)
            val setMethods = arrayOfNulls<Method>(nestedCount - 1)

            var currentClass: Class<*> = theClass
            for (i in 0 until nestedCount - 1) {
                val getter = ReflectionUtils.getClassGetter(currentClass, propertyParts[i])
                    ?: throw PropertyAccessException(
                        null,
                        String.format(
                            "Intermediate getter property not found for nesetd property `%s`",
                            propertyName
                        ),
                        false,
                        theClass,
                        propertyParts[i]
                    )

                getMethods[i] = getter
                setMethods[i] = ReflectionUtils.getClassSetter(currentClass, propertyParts[i], getter)

                currentClass = getMethods[i]!!.returnType
            }

            val method = setterMethod(currentClass, propertyParts[nestedCount - 1])
            if (method != null) {
                ReflectionUtils.makePublic(method)
                @Suppress("UNCHECKED_CAST")
                return NestedSetter(
                    theClass,
                    getMethods as Array<Method>,
                    setMethods as Array<Method>,
                    method,
                    propertyName
                )
            }

            var setter = getSetterOrNull(theClass.superclass, propertyName)
            if (setter == null) {
                val interfaces = theClass.interfaces
                for (i in interfaces.indices) {
                    setter = getSetterOrNull(interfaces[i], propertyName)
                    if (setter != null) break
                }
            }

            return setter
        }

        private fun setterMethod(theClass: Class<*>, propertyName: String): Method? {
            val getter = ReflectionUtils.getClassGetter(theClass, propertyName)
            return ReflectionUtils.getClassSetter(theClass, propertyName, getter)
        }
    }
}
