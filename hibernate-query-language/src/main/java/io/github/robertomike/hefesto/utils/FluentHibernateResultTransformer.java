package io.github.robertomike.hefesto.utils;

import io.github.robertomike.hefesto.exceptions.HefestoException;
import org.hibernate.transform.BasicTransformerAdapter;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class FluentHibernateResultTransformer extends BasicTransformerAdapter {
    private final Class<?> resultClass;
    private NestedSetter[] setters;
    private Constructor<?>[] constructors;

    public FluentHibernateResultTransformer(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if (tuple.length > 0 && tuple[0] != null && tuple[0].getClass().equals(resultClass)) {
            return tuple[0];
        }

        createCachedConstructors(resultClass, tuple.length);

        if (validConstructorForTuple(constructors, tuple)) {
            return createObject(constructors, tuple);
        }

        aliases = Arrays.stream(aliases)
                .map(a -> a.replace("_", "."))
                .toArray(String[]::new);

        createCachedSetters(resultClass, aliases);

        Object result = ClassUtils.newInstance(resultClass);

        for (int i = 0; i < aliases.length; i++) {
            setters[i].set(result, tuple[i]);
        }

        return result;
    }

    private Object createObject(Constructor<?>[] constructors, Object[] tuple) {
        Constructor<?> constructor = getConstructorForTuple(constructors, tuple);
        try {
            return constructor.newInstance(tuple);
        } catch (Exception e) {
            throw new HefestoException("Problem while creating object with constructor", e);
        }
    }

    private boolean validConstructorForTuple(Constructor<?>[] constructors, Object[] tuple) {
        return getConstructorForTuple(constructors, tuple) != null;
    }

    private Constructor<?> getConstructorForTuple(Constructor<?>[] constructors, Object[] tuple) {
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();

            boolean valid = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (tuple[i] == null || !parameterTypes[i].equals(tuple[i].getClass())) {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                return constructor;
            }
        }
        return null;
    }

    private void createCachedConstructors(Class<?> resultClass, int length) {
        if (constructors == null) {
            constructors = Arrays.stream(resultClass.getConstructors())
                    .filter(constructor -> constructor.getParameterCount() == length)
                    .toArray(Constructor[]::new);
        }
    }

    private void createCachedSetters(Class<?> resultClass, String[] aliases) {
        if (setters == null) {
            setters = createSetters(resultClass, aliases);
        }
    }

    private static NestedSetter[] createSetters(Class<?> resultClass, String[] aliases) {
        NestedSetter[] result = new NestedSetter[aliases.length];

        for (int i = 0; i < aliases.length; i++) {
            result[i] = NestedSetter.create(resultClass, aliases[i]);
        }

        return result;
    }

}