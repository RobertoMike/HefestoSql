package io.github.robertomike.hefesto.hefesto.constructors;

import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.BaseTest;
import io.github.robertomike.hefesto.hefesto.models.User;
import io.github.robertomike.hefesto.hefesto.models.UserPet;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Join functionality in Criteria Builder module.
 * Tests both traditional joins and new lambda-based JoinBuilder API.
 */
@ExtendWith(BaseTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinBuilderTest {
    
    // ================================
    // Basic Join Tests
    // ================================
    
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
    
    // ================================
    // Join Type Tests
    // ================================
    
    @Test
    @Order(3)
    void leftJoinTest() {
        var result = Hefesto.make(User.class)
                .join("addresses", JoinOperator.LEFT)
                .get();

        assertNotNull(result);
    }
    
    @Test
    @Order(4)
    void rightJoinTest() {
        var result = Hefesto.make(User.class)
                .join("addresses", JoinOperator.RIGHT)
                .get();

        assertNotNull(result);
    }
    
    @Test
    @Order(5)
    void innerJoinTest() {
        var result = Hefesto.make(User.class)
                .join("pets", JoinOperator.INNER)
                .get();

        assertFalse(result.isEmpty());
    }
    
    // ================================
    // Multiple Join Tests
    // ================================
    
    @Test
    @Order(6)
    void multipleJoinsWithDifferentTypes() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .join("addresses", JoinOperator.LEFT)
                .get();

        assertNotNull(result);
    }
    
    @Test
    @Order(7)
    void chainedJoins() {
        var result = Hefesto.make(UserPet.class)
                .join("user")
                .join("pet")
                .where("user.name", "John")
                .get();

        assertNotNull(result);
    }
    
    // ================================
    // Join with Where Tests
    // ================================
    
    @Test
    @Order(8)
    void joinWithWhereCondition() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .where("pets.name", "Fluffy")
                .get();

        assertNotNull(result);
    }
    
    @Test
    @Order(9)
    void multipleJoinsWithWhereConditions() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .join("addresses", JoinOperator.LEFT)
                .where("name", "John")
                .get();

        assertNotNull(result);
    }
    
    // ================================
    // Join with Fetch Tests
    // ================================
    
    @Test
    @Order(10)
    void joinWithFetch() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .with("pets")
                .get();

        assertFalse(result.isEmpty());
    }
    
    @Test
    @Order(11)
    void multipleFetches() {
        var result = Hefesto.make(User.class)
                .with("pets", "addresses")
                .get();

        assertFalse(result.isEmpty());
    }
    
    // ================================
    // Edge Cases
    // ================================
    
    @Test
    @Order(12)
    void joinWithoutWhereCondition() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .get();

        assertFalse(result.isEmpty());
    }
    
    @Test
    @Order(13)
    void multipleJoinsToSameEntity() {
        var result = Hefesto.make(UserPet.class)
                .join("user")
                .join("pet")
                .get();

        assertNotNull(result);
    }
    
    // ================================
    // Advanced Join Tests
    // ================================
    
    @Test
    @Order(14)
    void joinWithSelectCount() {
        var builder = Hefesto.make(User.class)
                .join("pets")
                .count();

        assertNotNull(builder);
    }
    
    @Test
    @Order(15)
    void joinWithPagination() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .limit(5)
                .get();

        assertNotNull(result);
        assertTrue(result.size() <= 5);
    }
}
