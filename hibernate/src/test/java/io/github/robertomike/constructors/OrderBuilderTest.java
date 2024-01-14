package io.github.robertomike.constructors;

import io.github.robertomike.BaseTest;
import io.github.robertomike.builders.Hefesto;
import io.github.robertomike.models.User;
import io.github.robertomike.enums.Sort;
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
