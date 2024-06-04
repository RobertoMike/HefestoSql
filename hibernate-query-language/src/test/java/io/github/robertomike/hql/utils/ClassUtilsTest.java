package io.github.robertomike.hql.utils;

import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.exceptions.HefestoException;
import io.github.robertomike.hefesto.utils.ClassUtils;
import io.github.robertomike.hql.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(BaseTest.class)
public class ClassUtilsTest {
    @Test
    void newInstance() {
        assertThrows(
                HefestoException.class,
                () -> ClassUtils.newInstance(Hefesto.class)
        );
    }
}
