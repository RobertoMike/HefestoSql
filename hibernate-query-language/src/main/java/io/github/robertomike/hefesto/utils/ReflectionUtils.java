package io.github.robertomike.hefesto.utils;

import lombok.NoArgsConstructor;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ReflectionUtils {
    public static void makePublic(AccessibleObject accessibleObject) {
        if (accessibleObject != null) {
            accessibleObject.setAccessible(true);
        }
    }

    /**
     * Try to find a class getter method by a property name. Don't check parent classes or
     * interfaces.
     *
     * @param classToCheck a class in which find a getter
     * @param propertyName a property name
     * @return the getter method or null, if such getter is not exist
     */
    public static Method getClassGetter(Class<?> classToCheck, String propertyName) {
        PropertyDescriptor[] descriptors = getPropertyDescriptors(classToCheck);

        for (PropertyDescriptor descriptor : descriptors) {
            if (isGetter(descriptor, propertyName)) {
                return descriptor.getReadMethod();
            }
        }

        return null;
    }

    private static boolean isGetter(PropertyDescriptor descriptor, String propertyName) {
        Method method = descriptor.getReadMethod();
        return method != null && method.getParameterTypes().length == 0
                && descriptor.getName().equalsIgnoreCase(propertyName);
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
    public static Method getClassSetter(Class<?> classToCheck, String propertyName,
                                        Method getterMethod) {
        return getClassSetter(classToCheck, propertyName,
                getterMethod == null ? null : getterMethod.getReturnType());
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
    public static Method getClassSetter(Class<?> classToCheck, String propertyName,
                                        Class<?> propertyType) {
        PropertyDescriptor[] descriptors = getPropertyDescriptors(classToCheck);

        for (PropertyDescriptor descriptor : descriptors) {
            if (isSetter(descriptor, propertyName, propertyType)) {
                return descriptor.getWriteMethod();
            }

        }

        return null;
    }

    private static boolean isSetter(PropertyDescriptor descriptor, String propertyName,
                                    Class<?> propertyType) {
        Method method = descriptor.getWriteMethod();

        return method != null && method.getParameterTypes().length == 1
                && method.getName().startsWith("set")
                && descriptor.getName().equalsIgnoreCase(propertyName)
                && (propertyType == null || method.getParameterTypes()[0].equals(propertyType));
    }

    private static PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
        try {
            return Introspector.getBeanInfo(beanClass).getPropertyDescriptors();
        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String[] getPropertyParts(String property) {
        return StringUtils.splitByDot(property);
    }
}
