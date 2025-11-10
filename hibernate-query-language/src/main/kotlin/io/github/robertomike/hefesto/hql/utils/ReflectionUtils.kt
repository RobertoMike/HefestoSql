package io.github.robertomike.hefesto.hql.utils

import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Method

/**
 * Utility object for Java reflection operations.
 * Provides methods for accessing and manipulating class properties via getter/setter methods.
 * 
 * This is primarily used internally for result transformation and DTO mapping.
 */
object ReflectionUtils {
    /**
     * Makes an AccessibleObject accessible by setting accessible flag to true.
     * Used to bypass Java access control checks.
     *
     * @param accessibleObject the object to make accessible (Field, Method, Constructor)
     */
    @JvmStatic
    fun makePublic(accessibleObject: AccessibleObject?) {
        accessibleObject?.isAccessible = true
    }

    /**
     * Finds a getter method for a property in a class.
     * Does not check parent classes or interfaces.
     *
     * @param classToCheck the class to search in
     * @param propertyName the property name to find getter for
     * @return the getter method, or null if not found
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
     * Finds a setter method for a property in a class, using getter method to determine type.
     * Does not check parent classes or interfaces.
     *
     * @param classToCheck the class to search in
     * @param propertyName the property name to find setter for
     * @param getterMethod the getter method to derive property type from
     * @return the setter method, or null if not found
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
     * Finds a setter method for a property in a class with explicit type specification.
     * Does not check parent classes or interfaces.
     *
     * @param classToCheck the class to search in
     * @param propertyName the property name to find setter for
     * @param propertyType the expected type of the property (can be null for any type)
     * @return the setter method, or null if not found
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

    /**
     * Gets PropertyDescriptor array for a bean class using Java Beans Introspector.
     *
     * @param beanClass the class to introspect
     * @return array of property descriptors
     */
    private fun getPropertyDescriptors(beanClass: Class<*>): Array<PropertyDescriptor> {
        return try {
            Introspector.getBeanInfo(beanClass).propertyDescriptors
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }

    /**
     * Splits a property path into its component parts.
     * For example, "user.address.city" becomes ["user", "address", "city"].
     *
     * @param property the property path with dot notation
     * @return array of property name parts
     */
    @JvmStatic
    fun getPropertyParts(property: String): Array<String> {
        return StringUtils.splitByDot(property)
    }
}
