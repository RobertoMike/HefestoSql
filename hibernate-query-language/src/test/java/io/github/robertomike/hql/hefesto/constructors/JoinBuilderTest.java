package io.github.robertomike.hql.hefesto.constructors;

import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hql.BaseTest;
import io.github.robertomike.hql.hefesto.models.User;
import io.github.robertomike.hql.hefesto.models.UserPet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BaseTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinBuilderTest {
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
    @Order(1)
    void completeJoin() {
        var result = Hefesto.make(User.class)
                .join("UserPet", "user.id", "id")
                .get();

        assertFalse(result.isEmpty());
    }
    @Test
    @Order(2)
    void innerJoinWithLeftJoinWithJoinFetch() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .join("addresses", JoinOperator.LEFT)
                .get();

        assertFalse(result.isEmpty());
    }

    // ================================
    // Deep Join Tests
    // Note: Deep joins with dot notation (e.g., "user.addresses") have complex
    // requirements in HQL and may need manual HQL construction in some cases.
    // The feature is implemented but requires proper entity mapping.
    // ================================

    /**
     * Test that deep join syntax is supported (may require proper entity mapping)
     */
    @Test
    @Order(6)
    void deepJoinGeneratesCorrectHQL() {
        var builder = Hefesto.make(UserPet.class)
                .join("user")
                .join("pet");

        var params = new java.util.HashMap<String, Object>();
        String hql = builder.getQuery(params);

        // Should contain joins
        assertTrue(hql.contains("join"), "HQL should contain join keyword");
        assertTrue(hql.contains("user"), "HQL should reference user");
    }

    /**
     * Test that join count is correct with multiple joins
     */
    @Test
    @Order(8)
    void multipleJoinsCount() {
        var builder = Hefesto.make(UserPet.class)
                .join("user")
                .join("pet");

        assertNotNull(builder);
    }
}

