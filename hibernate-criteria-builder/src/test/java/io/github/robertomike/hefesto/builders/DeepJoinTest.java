package io.github.robertomike.hefesto.builders;

import io.github.robertomike.hefesto.BaseTest;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.hefesto.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BaseTest.class)
class DeepJoinTest {

    @Test
    void testDeepJoinWithDotNotation() {
        // Test creating a deep join using dot notation
        var hefesto = Hefesto.make(User.class)
                .joinDeep("addresses.city");

        assertNotNull(hefesto);
        assertFalse(hefesto.getJoins().getJoinDefinitions().isEmpty());
        
        var join = hefesto.getJoins().getJoinDefinitions().get(0);
        assertEquals("addresses", join.getTable());
        assertTrue(join.hasDeepJoins());
        assertEquals(1, join.getDeepJoins().size());
        assertEquals("city", join.getDeepJoins().get(0).getTable());
    }

    @Test
    void testDeepJoinWithDotNotationAndAlias() {
        // Test creating a deep join with alias for the final join
        var hefesto = Hefesto.make(User.class)
                .joinDeep("addresses.city", "userCity");

        var join = hefesto.getJoins().getJoinDefinitions().get(0);
        assertEquals("addresses", join.getTable());
        
        var nestedJoin = join.getDeepJoins().get(0);
        assertEquals("city", nestedJoin.getTable());
        assertEquals("userCity", nestedJoin.getAlias());
    }

    @Test
    void testDeepJoinWithDotNotationAndOperator() {
        // Test creating a deep join with specific operator
        var hefesto = Hefesto.make(User.class)
                .joinDeep("addresses.city.country", JoinOperator.LEFT);

        var join = hefesto.getJoins().getJoinDefinitions().get(0);
        assertEquals("addresses", join.getTable());
        assertEquals(JoinOperator.LEFT, join.getJoinOperator());
        
        var cityJoin = join.getDeepJoins().get(0);
        assertEquals("city", cityJoin.getTable());
        assertEquals(JoinOperator.LEFT, cityJoin.getJoinOperator());
        
        var countryJoin = cityJoin.getDeepJoins().get(0);
        assertEquals("country", countryJoin.getTable());
        assertEquals(JoinOperator.LEFT, countryJoin.getJoinOperator());
    }

    @Test
    void testManualDeepJoinConstruction() {
        // Test manually building deep join structure using factory methods
        var storeJoin = io.github.robertomike.hefesto.actions.Join.make("store");
        var userJoin = io.github.robertomike.hefesto.actions.Join.make("user", JoinOperator.LEFT);
        var brandJoin = io.github.robertomike.hefesto.actions.Join.make("brands", JoinOperator.RIGHT);
        
        userJoin.getDeepJoins().add(brandJoin);
        storeJoin.getDeepJoins().add(userJoin);

        var hefesto = Hefesto.make(User.class)
                .join(storeJoin);

        var rootJoin = hefesto.getJoins().getJoinDefinitions().get(0);
        assertEquals("store", rootJoin.getTable());
        assertEquals(JoinOperator.INNER, rootJoin.getJoinOperator());
        
        var nestedUser = rootJoin.getDeepJoins().get(0);
        assertEquals("user", nestedUser.getTable());
        assertEquals(JoinOperator.LEFT, nestedUser.getJoinOperator());
        
        var nestedBrand = nestedUser.getDeepJoins().get(0);
        assertEquals("brands", nestedBrand.getTable());
        assertEquals(JoinOperator.RIGHT, nestedBrand.getJoinOperator());
    }

    @Test
    void testMakeDeepStaticMethod() {
        // Test the static makeDeep method
        var join = io.github.robertomike.hefesto.actions.Join.makeDeep("user.brands.settings");
        
        assertEquals("user", join.getTable());
        assertEquals(JoinOperator.INNER, join.getJoinOperator());
        
        var brandsJoin = join.getDeepJoins().get(0);
        assertEquals("brands", brandsJoin.getTable());
        
        var settingsJoin = brandsJoin.getDeepJoins().get(0);
        assertEquals("settings", settingsJoin.getTable());
    }

    @Test
    void testMakeDeepWithAliasStaticMethod() {
        // Test the static makeDeep method with alias
        var join = io.github.robertomike.hefesto.actions.Join.makeDeep("user.brands.settings", "userSettings", JoinOperator.LEFT);
        
        assertEquals("user", join.getTable());
        assertEquals(JoinOperator.LEFT, join.getJoinOperator());
        
        var brandsJoin = join.getDeepJoins().get(0);
        assertEquals("brands", brandsJoin.getTable());
        
        var settingsJoin = brandsJoin.getDeepJoins().get(0);
        assertEquals("settings", settingsJoin.getTable());
        assertEquals("userSettings", settingsJoin.getAlias());
    }

    @Test
    void testJoinHelperMethods() {
        // Test the helper methods on Join
        var simpleJoin = io.github.robertomike.hefesto.actions.Join.make("user");
        assertFalse(simpleJoin.hasDeepJoins());
        assertFalse(simpleJoin.isCustomJoin());
        
        var customJoin = io.github.robertomike.hefesto.actions.Join.make("store", "storeId", "id");
        assertTrue(customJoin.isCustomJoin());
        assertFalse(customJoin.hasDeepJoins());
        
        var deepJoin = io.github.robertomike.hefesto.actions.Join.makeDeep("user.brands");
        assertTrue(deepJoin.hasDeepJoins());
        assertFalse(deepJoin.isCustomJoin());
    }

    @Test
    void testWithDeepFluentAPI() {
        // Test the fluent withDeep API
        var storeJoin = io.github.robertomike.hefesto.actions.Join.make("store");
        var userJoin = storeJoin.withDeep("users");
        var brandJoin = userJoin.withDeep("brands", JoinOperator.LEFT);

        // Verify the structure: store -> users -> brands
        assertEquals("store", storeJoin.getTable());
        assertTrue(storeJoin.hasDeepJoins());
        assertEquals(1, storeJoin.getDeepJoins().size());
        
        assertSame(userJoin, storeJoin.getDeepJoins().get(0));
        assertEquals("users", userJoin.getTable());
        assertEquals(JoinOperator.INNER, userJoin.getJoinOperator());
        
        assertSame(brandJoin, userJoin.getDeepJoins().get(0));
        assertEquals("brands", brandJoin.getTable());
        assertEquals(JoinOperator.LEFT, brandJoin.getJoinOperator());
    }

    @Test
    void testWithDeepChaining() {
        // Test chaining withDeep calls directly
        var join = io.github.robertomike.hefesto.actions.Join.make("store");
        join.withDeep("users")
            .withDeep("brands", JoinOperator.RIGHT)
            .withDeep("settings");

        // Verify structure: store -> users -> brands -> settings
        assertEquals("store", join.getTable());
        
        var users = join.getDeepJoins().get(0);
        assertEquals("users", users.getTable());
        
        var brands = users.getDeepJoins().get(0);
        assertEquals("brands", brands.getTable());
        assertEquals(JoinOperator.RIGHT, brands.getJoinOperator());
        
        var settings = brands.getDeepJoins().get(0);
        assertEquals("settings", settings.getTable());
    }

    @Test
    void testWithDeepWithAlias() {
        // Test withDeep with alias parameter
        var join = io.github.robertomike.hefesto.actions.Join.make("store");
        var userJoin = join.withDeep("users", "usr");
        var brandJoin = userJoin.withDeep("brands", "brnd", JoinOperator.LEFT);

        assertEquals("usr", userJoin.getAlias());
        assertEquals("brnd", brandJoin.getAlias());
        assertEquals(JoinOperator.LEFT, brandJoin.getJoinOperator());
    }

    @Test
    void testWithDeepMultipleBranches() {
        // Test that each join can have multiple deep joins (branching)
        var storeJoin = io.github.robertomike.hefesto.actions.Join.make("store");
        var userJoin = storeJoin.withDeep("users");
        var productJoin = storeJoin.withDeep("products");

        assertEquals(2, storeJoin.getDeepJoins().size());
        assertEquals("users", storeJoin.getDeepJoins().get(0).getTable());
        assertEquals("products", storeJoin.getDeepJoins().get(1).getTable());
    }

    @Test
    void testWithDeepReturnsNewJoin() {
        // Verify withDeep always returns a new Join instance
        var parent = io.github.robertomike.hefesto.actions.Join.make("parent");
        var child1 = parent.withDeep("child1");
        var child2 = parent.withDeep("child2");

        assertNotSame(child1, child2);
        assertNotSame(parent, child1);
        assertNotSame(parent, child2);
    }

    @Test
    void testWithDeepInHefestoBuilder() {
        // Test using withDeep in the Hefesto builder
        var storeJoin = io.github.robertomike.hefesto.actions.Join.make("store");
        storeJoin.withDeep("users")
                 .withDeep("brands");

        var hefesto = Hefesto.make(User.class)
                .join(storeJoin);

        var joinDefs = hefesto.getJoins().getJoinDefinitions();
        assertEquals(1, joinDefs.size());
        
        var join = joinDefs.get(0);
        assertEquals("store", join.getTable());
        assertTrue(join.hasDeepJoins());
    }
}
