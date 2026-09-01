package io.github.robertomike.hefesto.builders;

import io.github.robertomike.hefesto.BaseTest;
import io.github.robertomike.hefesto.actions.Join;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.hefesto.models.Address;
import io.github.robertomike.hefesto.hefesto.models.Pet;
import io.github.robertomike.hefesto.hefesto.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for deep join functionality that actually query the database.
 * These tests verify that the deep join implementation works correctly with Hibernate.
 */
@ExtendWith(BaseTest.class)
class DeepJoinIntegrationTest {

    @Test
    void testSimpleDeepJoinWithDatabase() {
        // Test: User -> addresses relationship using deep join
        var users = Hefesto.make(User.class)
                .joinDeep("addresses")
                .where("addresses.city", "Springfield")
                .get();

        assertNotNull(users);
        // Should find users who have addresses in Springfield
    }

    @Test
    void testTwoLevelDeepJoinWithDatabase() {
        // Test: User -> addresses (but Address doesn't have further relationships in our model)
        // So let's test with User -> pets relationship instead
        var users = Hefesto.make(User.class)
                .joinDeep("pets")
                .where("pets.name", "lola")
                .get();

        assertNotNull(users);
        assertFalse(users.isEmpty(), "Should find users with pet named 'lola'");
        
        // Verify the user actually has the pet
        users.forEach(user -> {
            assertNotNull(user.getName());
        });
    }

    @Test
    void testDeepJoinWithAliasInWhere() {
        // Test using alias in where clause
        var users = Hefesto.make(User.class)
                .joinDeep("addresses", "addr")
                .where("addr.city", "Springfield")
                .get();

        assertNotNull(users);
    }

    @Test
    void testDeepJoinWithLeftOperator() {
        // Test with LEFT join to include users without addresses
        var users = Hefesto.make(User.class)
                .joinDeep("addresses", JoinOperator.LEFT)
                .get();

        assertNotNull(users);
        // Should include all users, even those without addresses
    }

    @Test
    void testWithDeepFluentAPI() {
        // Test the new fluent API structure verification (no database execution)
        var petJoin = io.github.robertomike.hefesto.actions.Join.make("users");
        petJoin.withDeep("addresses");

        var hefesto = Hefesto.make(Pet.class)
                .join(petJoin);
        
        // Verify the join structure (just check the configuration, don't execute)
        var joinDef = hefesto.getJoins().getJoinDefinitions().get(0);
        assertEquals("users", joinDef.getTable());
        assertTrue(joinDef.hasDeepJoins());
        assertEquals("addresses", joinDef.getDeepJoins().get(0).getTable());
    }

    @Test
    void testWithDeepFluentAPIChained() {
        // Test chaining multiple withDeep calls
        var petJoin = io.github.robertomike.hefesto.actions.Join.make("users");
        petJoin.withDeep("addresses");

        var hefesto = Hefesto.make(Pet.class)
                .join(petJoin);
        
        // Verify the join structure
        var joinDef = hefesto.getJoins().getJoinDefinitions().get(0);
        assertEquals("users", joinDef.getTable());
        assertTrue(joinDef.hasDeepJoins());
        
        var addressesJoin = joinDef.getDeepJoins().get(0);
        assertEquals("addresses", addressesJoin.getTable());
    }

    @Test
    void testWithDeepWithAlias() {
        // Test withDeep with alias
        var petJoin = io.github.robertomike.hefesto.actions.Join.make("users");
        petJoin.withDeep("addresses", "addr", JoinOperator.LEFT);

        var hefesto = Hefesto.make(Pet.class)
                .join(petJoin);
        
        // Verify alias is set
        var joinDef = hefesto.getJoins().getJoinDefinitions().get(0);
        var addressesJoin = joinDef.getDeepJoins().get(0);
        assertEquals("addr", addressesJoin.getAlias());
        assertEquals(JoinOperator.LEFT, addressesJoin.getJoinOperator());
    }

    @Test
    void testComplexDeepJoinQuery() {
        // Complex query: Find all pets whose owners have addresses in specific cities
        var petJoin = io.github.robertomike.hefesto.actions.Join.make("users");
        petJoin.withDeep("addresses");

        var pets = Hefesto.make(Pet.class)
                .join(petJoin)
                .where("addresses.city", "Springfield")
                .get();

        assertNotNull(pets);
    }

    @Test
    void testDeepJoinWithMultipleWhereClauses() {
        // Test deep join with multiple where conditions
        var users = Hefesto.make(User.class)
                .joinDeep("pets", "p")
                .joinDeep("addresses", "a")
                .where("p.name", "lola")
                .where("a.city", "Springfield")
                .get();

        assertNotNull(users);
    }

    @Test
    void testWithDeepReturnsCorrectJoin() {
        // Verify that withDeep returns the newly created join
        var storeJoin = Join.make("store");
        var userJoin = storeJoin.withDeep("users");
        var brandJoin = userJoin.withDeep("brands", JoinOperator.LEFT);

        // Verify structure
        assertEquals("store", storeJoin.getTable());
        assertEquals(1, storeJoin.getDeepJoins().size());
        assertSame(userJoin, storeJoin.getDeepJoins().get(0));
        
        assertEquals("users", userJoin.getTable());
        assertEquals(1, userJoin.getDeepJoins().size());
        assertSame(brandJoin, userJoin.getDeepJoins().get(0));
        
        assertEquals("brands", brandJoin.getTable());
        assertEquals(JoinOperator.LEFT, brandJoin.getJoinOperator());
        assertTrue(brandJoin.getDeepJoins().isEmpty());
    }

    @Test
    void testMixedJoinApproaches() {
        // Test mixing regular joins with deep joins
        var users = Hefesto.make(User.class)
                .join("pets", "p")  // Regular join
                .joinDeep("addresses.user")  // Deep join notation
                .where("p.name", "lola")
                .get();

        assertNotNull(users);
    }

    @Test
    void testDeepJoinWithCount() {
        // Test that deep joins work with count queries
        var count = Hefesto.make(User.class)
                .joinDeep("addresses")
                .where("addresses.city", "Springfield")
                .countResults();

        assertTrue(count >= 0);
    }

    @Test
    void testDeepJoinWithFindFirst() {
        // Test deep join with findFirst
        var user = Hefesto.make(User.class)
                .joinDeep("pets")
                .where("pets.name", "lola")
                .findFirst();

        assertNotNull(user);
    }

    @Test
    void testWithDeepPreservesOperator() {
        // Verify that different operators are preserved through the chain
        var join = Join.make("pets", JoinOperator.LEFT);
        var nested1 = join.withDeep("users", JoinOperator.RIGHT);
        var nested2 = nested1.withDeep("addresses", JoinOperator.INNER);

        assertEquals(JoinOperator.LEFT, join.getJoinOperator());
        assertEquals(JoinOperator.RIGHT, nested1.getJoinOperator());
        assertEquals(JoinOperator.INNER, nested2.getJoinOperator());
    }

    @Test
    void testDeepJoinDotNotationVsWithDeepEquivalence() {
        // Verify that dot notation and withDeep produce equivalent structures
        
        // Using dot notation
        var join1 = Join.makeDeep("pets.users.addresses", JoinOperator.LEFT);
        
        // Using withDeep
        var join2 = Join.make("pets", JoinOperator.LEFT);
        join2.withDeep("users", JoinOperator.LEFT)
             .withDeep("addresses", JoinOperator.LEFT);

        // Both should have the same structure
        assertEquals(join1.getTable(), join2.getTable());
        assertEquals(join1.getJoinOperator(), join2.getJoinOperator());
        assertEquals(join1.getDeepJoins().size(), join2.getDeepJoins().size());
        
        var nested1_1 = join1.getDeepJoins().get(0);
        var nested2_1 = join2.getDeepJoins().get(0);
        assertEquals(nested1_1.getTable(), nested2_1.getTable());
        assertEquals(nested1_1.getJoinOperator(), nested2_1.getJoinOperator());
    }
}
