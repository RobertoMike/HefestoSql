package io.github.robertomike.hibernate.hefesto.constructors;

import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.Sort;
import io.github.robertomike.hibernate.BaseTest;
import io.github.robertomike.hibernate.hefesto.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(BaseTest.class)
public class OrderBuilderTest {
    @Test
    void orderBy() {
        var result = Hefesto.make(User.class)
                .orderBy("name")
                .get();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
    @Test
    void orderByDesc() {
        var result = Hefesto.make(User.class)
                .orderBy("id", Sort.DESC)
                .get();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
    @Test
    void orderByAsc() {
        var result = Hefesto.make(User.class)
                .orderBy("email", Sort.ASC)
                .get();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
