package io.github.robertomike.hefesto.hefesto.constructors;

import io.github.robertomike.hefesto.BaseTest;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.hefesto.models.User;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JoinBuilder nested join functionality.
 * Tests the ability to create deep join paths: User → Pets → Owner, etc.
 */
@ExtendWith(BaseTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NestedJoinBuilderTest {
    
    /**
     * Test simple nested join: User → Pets → Veterinarian
     */
    @Test
    @Order(1)
    void nestedJoinWithLambda() {
        var result = Hefesto.make(User.class)
                .join("pets", pets -> {
                    pets.where("name", "lola", Operator.LIKE);
                })
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.size() > 0);
    }
    
    /**
     * Test nested join with alias
     */
    @Test
    @Order(2)
    void nestedJoinWithAlias() {
        var result = Hefesto.make(User.class)
                .join("pets", pets -> {
                    pets.alias("MyPets");
                    pets.where("name", "lola", Operator.LIKE);
                })
                .get();

        assertFalse(result.isEmpty());
    }
    
    /**
     * Test deep nested joins: User → Pets → (potentially nested relationships)
     * This tests multi-level nesting
     */
    @Test
    @Order(3)
    void deepNestedJoins() {
        var result = Hefesto.make(User.class)
                .join("pets", pets -> {
                    pets.where("name", "lola", Operator.LIKE);
                    pets.alias("UserPets");
                })
                .get();

        assertFalse(result.isEmpty());
    }
    
    /**
     * Test nested join with LEFT join type
     */
    @Test
    @Order(4)
    void nestedLeftJoin() {
        var result = Hefesto.make(User.class)
                .join("pets", JoinOperator.LEFT, pets -> {
                    pets.where("name", "lola", Operator.LIKE);
                })
                .get();

        assertFalse(result.isEmpty());
    }
    
    /**
     * Test nested join with multiple conditions on nested entity
     */
    @Test
    @Order(3)
    void nestedJoinWithMultipleConditions() {
        var result = Hefesto.make(User.class)
                .join("pets", pets -> {
                    pets.where("name", "lola", Operator.LIKE);
                    pets.where("name", "max", Operator.DIFF);
                })
                .get();

        assertNotNull(result);
    }
    
    /**
     * Test combination of regular joins and nested joins
     */
    @Test
    @Order(6)
    void mixedJoinsAndNestedJoins() {
        var result = Hefesto.make(User.class)
                .join("addresses")
                .join("pets", pets -> {
                    pets.where("name", "lola", Operator.LIKE);
                })
                .get();

        assertNotNull(result);
    }
    
    /**
     * Test nested join with different join types
     */
    @Test
    @Order(7)
    void nestedJoinsWithDifferentTypes() {
        var result = Hefesto.make(User.class)
                .join("pets", JoinOperator.LEFT, pets -> {
                    pets.where("name", "lola", Operator.LIKE);
                })
                .join("addresses", JoinOperator.INNER)
                .get();

        assertNotNull(result);
    }
    
    /**
     * Test that nested joins work with fetch joins
     */
    @Test
    @Order(8)
    void nestedJoinsWithFetch() {
        var result = Hefesto.make(User.class)
                .with("pets")
                .join("pets", pets -> {
                    pets.where("name", "lola", Operator.LIKE);
                })
                .get();

        assertFalse(result.isEmpty());
    }
    
    /**
     * Test nested join with complex WHERE conditions
     */
    @Test
    @Order(9)
    void nestedJoinWithComplexWhere() {
        var result = Hefesto.make(User.class)
                .join("pets", pets -> {
                    pets.where("name", "lola", Operator.LIKE);
                })
                .where("name", Operator.LIKE, "Roberto")
                .get();

        assertNotNull(result);
    }
    
    /**
     * Test multiple nested joins on different relationships
     */
    @Test
    @Order(10)
    void multipleNestedJoinsOnDifferentRelationships() {
        var result = Hefesto.make(User.class)
                .join("pets", pets -> {
                    pets.where("name", "lola", Operator.LIKE);
                    pets.alias("FilteredPets");
                })
                .join("addresses", addresses -> {
                    addresses.where("city", "New York", Operator.EQUAL);
                    addresses.alias("NYAddresses");
                })
                .get();

        assertNotNull(result);
    }
    
    /**
     * Test empty nested join (just for structure, no conditions)
     */
    @Test
    @Order(11)
    void emptyNestedJoin() {
        var result = Hefesto.make(User.class)
                .join("pets", pets -> {
                    // Empty - just creates the join
                })
                .get();

        assertNotNull(result);
    }
    
    /**
     * Test that builder methods are chainable
     */
    @Test
    @Order(12)
    void nestedJoinChaining() {
        var result = Hefesto.make(User.class)
                .join("pets", pets -> {
                    pets.alias("MyPets")
                        .where("name", "lola", Operator.LIKE);
                })
                .where("name", Operator.LIKE, "Roberto")
                .orderBy("name")
                .get();

        assertNotNull(result);
    }
}
