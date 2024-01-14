package io.github.robertomike.constructors;

import io.github.robertomike.BaseTest;
import io.github.robertomike.builders.Hefesto;
import io.github.robertomike.models.User;
import io.github.robertomike.enums.JoinOperator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(BaseTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinBuilderTest {
    @Test
    @Order(0)
    void rightJoin() {
        var result = Hefesto.make(User.class)
                .join("pets", JoinOperator.RIGHT)
                .get();

        assertFalse(result.isEmpty());
    }
    @Test
    @Order(1)
    void innerJoinWithLeftJoin() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .join("addresses", JoinOperator.LEFT)
                .get();

        assertFalse(result.isEmpty());
    }
    @Test
    @Order(2)
    void innerJoinWithLeftJoinWithJoinFetch() {
        var result = Hefesto.make(User.class)
                .with("pets", "addresses")
                .join("pets")
                .join("addresses", JoinOperator.LEFT)
                .get();

        assertFalse(result.isEmpty());
    }
}
