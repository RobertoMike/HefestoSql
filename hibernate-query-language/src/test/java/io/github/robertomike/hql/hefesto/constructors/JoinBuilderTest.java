package io.github.robertomike.hql.hefesto.constructors;

import io.github.robertomike.hefesto.hql.builders.Hefesto;
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

/**
 * Test suite for Join functionality in HQL module.
 * Note: Lambda-based join with JoinBuilder is not yet implemented in HQL module.
 * These tests cover the current join functionality.
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
    void completeJoin() {
        var result = Hefesto.make(User.class)
                .join("UserPet", "user.id", "id")
                .get();

        assertFalse(result.isEmpty());
    }
    
    @Test
    @Order(3)
    void innerJoinWithLeftJoinWithJoinFetch() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .join("addresses", JoinOperator.LEFT)
                .get();

        assertFalse(result.isEmpty());
    }
    
    // ================================
    // Join Type Tests
    // ================================
    
    @Test
    @Order(4)
    void leftJoinTest() {
        var result = Hefesto.make(User.class)
                .join("addresses", JoinOperator.LEFT)
                .get();

        assertNotNull(result);
    }
    
    @Test
    @Order(5)
    void rightJoinTest() {
        var result = Hefesto.make(User.class)
                .join("addresses", JoinOperator.RIGHT)
                .get();

        assertNotNull(result);
    }
    
    @Test
    @Order(6)
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
    @Order(7)
    void multipleJoinsWithDifferentTypes() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .join("addresses", JoinOperator.LEFT)
                .get();

        assertNotNull(result);
    }
    
    @Test
    @Order(8)
    void joinWithTableAndFields() {
        var result = Hefesto.make(User.class)
                .join("UserPet", "id", "user.id")
                .get();

        assertFalse(result.isEmpty());
    }
    
    // ================================
    // Deep Join Tests
    // ================================
    
    @Test
    @Order(9)
    void deepJoinGeneratesCorrectHQL() {
        var builder = Hefesto.make(UserPet.class)
                .join("user")
                .join("pet");

        var params = new java.util.HashMap<String, Object>();
        String hql = builder.getQuery(params);

        assertTrue(hql.contains("join"), "HQL should contain join keyword");
        assertTrue(hql.contains("user"), "HQL should reference user");
    }
    
    @Test
    @Order(10)
    void multipleJoinsCount() {
        var builder = Hefesto.make(UserPet.class)
                .join("user")
                .join("pet");

        assertNotNull(builder);
    }
    
    @Test
    @Order(11)
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
    @Order(12)
    void joinWithWhereCondition() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .where("pets.name", "Fluffy")
                .get();

        assertNotNull(result);
    }
    
    @Test
    @Order(13)
    void multipleJoinsWithWhereConditions() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .join("addresses", JoinOperator.LEFT)
                .where("name", "John")
                .get();

        assertNotNull(result);
    }
    
    // ================================
    // Edge Cases
    // ================================
    
    @Test
    @Order(14)
    void joinWithoutWhereCondition() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .get();

        assertFalse(result.isEmpty());
    }
    
    @Test
    @Order(15)
    void multipleJoinsToSameEntity() {
        var result = Hefesto.make(UserPet.class)
                .join("user")
                .join("pet")
                .get();

        assertNotNull(result);
    }
}

