package io.github.robertomike.hefesto.utils

import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Method

object ReflectionUtils {
    @JvmStatic
    fun makePublic(accessibleObject: AccessibleObject?) {
        accessibleObject?.isAccessible = true
    }

    /**
     * Try to find a class getter method by a property name. Don't check parent classes or
     * interfaces.
     *
     * @param classToCheck a class in which find a getter
     * @param propertyName a property name
     * @return the getter method or null, if such getter is not exist
     */
    @JvmStatic
    fun getClassGetter(classToCheck: Class<*>, propertyName: String): Method? {
        val descriptors = getPropertyDescriptors(classToCheck)

        for (descriptor in descriptors) {
            if (isGetter(descriptor, propertyName)) {
                return descriptor.readMethod
            }
        }

        return null
    }

    private fun isGetter(descriptor: PropertyDescriptor, propertyName: String): Boolean {
        val method = descriptor.readMethod
        return method != null && method.parameterTypes.isEmpty()
                && descriptor.name.equals(propertyName, ignoreCase = true)
    }

    /**
     * Try to find a class setter method by a property name. Don't check parent classes or
     * interfaces.
     *
     * @param classToCheck a class in which find a setter
     * @param propertyName a property name
     * @param getterMethod a getter method for getting a type of a property
     * @return the setter method or null, if such setter is not exist
     */
    @JvmStatic
    fun getClassSetter(
        classToCheck: Class<*>,
        propertyName: String,
        getterMethod: Method?
    ): Method? {
        return getClassSetter(
            classToCheck, propertyName,
            getterMethod?.returnType
        )
    }

    /**
     * Try to find a class setter method by a property name. Don't check parent classes or
     * interfaces.
     *
     * @param classToCheck a class in which find a setter
     * @param propertyName a property name
     * @param propertyType a type of a property
     * @return the setter method or null, if such setter is not exist
     */
    @JvmStatic
    fun getClassSetter(
        classToCheck: Class<*>,
        propertyName: String,
        propertyType: Class<*>?
    ): Method? {
        val descriptors = getPropertyDescriptors(classToCheck)

        for (descriptor in descriptors) {
            if (isSetter(descriptor, propertyName, propertyType)) {
                return descriptor.writeMethod
            }
        }

        return null
    }

    private fun isSetter(
        descriptor: PropertyDescriptor,
        propertyName: String,
        propertyType: Class<*>?
    ): Boolean {
        val method = descriptor.writeMethod

        return method != null && method.parameterTypes.size == 1
                && method.name.startsWith("set")
                && descriptor.name.equals(propertyName, ignoreCase = true)
                && (propertyType == null || method.parameterTypes[0] == propertyType)
    }

    private fun getPropertyDescriptors(beanClass: Class<*>): Array<PropertyDescriptor> {
        return try {
            Introspector.getBeanInfo(beanClass).propertyDescriptors
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }

    @JvmStatic
    fun getPropertyParts(property: String): Array<String> {
        return StringUtils.splitByDot(property)
    }
}
