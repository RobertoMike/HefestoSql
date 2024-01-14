package io.github.robertomike.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HibernateModelTest {
    @Test
    void getTable() {
        assertEquals("users", new User().getTable());
    }
}
