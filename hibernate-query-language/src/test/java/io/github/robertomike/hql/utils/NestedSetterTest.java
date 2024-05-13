package io.github.robertomike.hql.utils;

import io.github.robertomike.hefesto.actions.wheres.Where;
import io.github.robertomike.hefesto.actions.wheres.WhereRaw;
import io.github.robertomike.hefesto.exceptions.HefestoException;
import io.github.robertomike.hefesto.utils.NestedSetter;
import io.github.robertomike.hql.BaseTest;
import org.hibernate.PropertyAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(BaseTest.class)
public class NestedSetterTest {
    @Test
    void set() {
        NestedSetter setter = NestedSetter.create(Where.class, "value");

        assertThrows(
                PropertyAccessException.class,
                () -> setter.set(new WhereRaw(""), "a")
        );
    }

    @Test
    void createWithFakeGetter() {
        assertThrows(
                PropertyAccessException.class,
                () -> NestedSetter.create(Where.class, "field2")
        );
    }

    @Test
    void createClassNull() {
        assertThrows(
                PropertyAccessException.class,
                () -> NestedSetter.create(null, "value")
        );
    }

    @Test
    void createParentGetter() {
        assertThrows(
                PropertyAccessException.class,
                () -> NestedSetter.create(null, "whereOperation")
        );
    }
}
