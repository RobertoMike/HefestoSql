package io.github.robertomike.hefesto.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HibernateModelTest {
    @Test
    void getTable() {
        assertEquals(User.class.getSimpleName(), new User().getTable());
    }
    @Test
    void getOriginalTable() {
        assertEquals("users", new User().getOriginalTable());
    }
}
