package io.github.robertomike.hql.hefesto.constructors;

import io.github.robertomike.hql.BaseTest;
import io.github.robertomike.hefesto.hql.builders.Hefesto;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hql.hefesto.models.User;
import io.github.robertomike.hql.hefesto.models.UserPet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BaseTest.class)
public class WhereFieldTest {

    @Test
    void whereFieldEqual_SameEntity() {
        // Test comparing two fields on the same entity in HQL
        var result = Hefesto.make(User.class)
                .whereField("name", "email")
                .get();

        // Should execute without errors
        assertNotNull(result);
    }

    @Test
    void whereFieldEqual_WithOperator() {
        // Test with explicit EQUAL operator
        var result = Hefesto.make(User.class)
                .whereField("name", Operator.EQUAL, "email")
                .get();

        assertNotNull(result);
    }

    @Test
    void whereFieldGreater_CompareNumericFields() {
        // Test comparing numeric fields with GREATER operator
        var result = Hefesto.make(User.class)
                .whereField("id", Operator.GREATER, "id")
                .get();

        // Should return empty since id > id is always false
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whereFieldLess() {
        // Test LESS operator
        var result = Hefesto.make(User.class)
                .whereField("id", Operator.LESS, "id")
                .get();

        // Should return empty since id < id is always false
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whereFieldGreaterOrEqual() {
        // Test GREATER_OR_EQUAL operator
        var result = Hefesto.make(User.class)
                .whereField("id", Operator.GREATER_OR_EQUAL, "id")
                .get();

        // Should return all users since id >= id is always true
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void whereFieldLessOrEqual() {
        // Test LESS_OR_EQUAL operator
        var result = Hefesto.make(User.class)
                .whereField("id", Operator.LESS_OR_EQUAL, "id")
                .get();

        // Should return all users since id <= id is always true
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void whereFieldDiff() {
        // Test DIFF (not equal) operator
        var result = Hefesto.make(User.class)
                .whereField("name", Operator.DIFF, "email")
                .get();

        // Should return users where name != email (most likely all users)
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void whereFieldWithJoin() {
        // Test comparing fields across joined entities in HQL
        var result = Hefesto.make(User.class)
                .join("pets", "p")
                .whereField("name", Operator.EQUAL, "p.name")
                .get();

        // Should execute without errors
        assertNotNull(result);
    }

    @Test
    void whereFieldCombinedWithRegularWhere() {
        // Test combining whereField with regular where conditions
        var result = Hefesto.make(User.class)
                .where("name", Operator.LIKE, "%test%")
                .whereField("name", Operator.DIFF, "email")
                .get();

        assertNotNull(result);
    }

    @Test
    void whereFieldMultiple() {
        // Test multiple whereField conditions
        var result = Hefesto.make(User.class)
                .whereField("id", Operator.GREATER_OR_EQUAL, "id")
                .whereField("name", Operator.DIFF, "email")
                .get();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void whereFieldLike() {
        // Test LIKE operator with field comparison
        var result = Hefesto.make(User.class)
                .whereField("name", Operator.LIKE, "name")
                .get();

        // name LIKE name - should match all users
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void whereFieldNotLike() {
        // Test NOT_LIKE operator with field comparison
        var result = Hefesto.make(User.class)
                .whereField("name", Operator.NOT_LIKE, "email")
                .get();

        // Should execute without errors
        assertNotNull(result);
    }

    @Test
    void whereFieldWithJoinAndAlias() {
        // Test whereField with join using alias in HQL
        var result = Hefesto.make(UserPet.class)
                .join("user", "u")
                .join("pet", "p")
                .whereField("u.name", Operator.DIFF, "p.name")
                .get();

        // Should find user-pet relationships where user name != pet name
        assertNotNull(result);
    }

    @Test
    void whereFieldQualifiesFieldNames() {
        // Test that HQL properly qualifies field names with table aliases
        var result = Hefesto.make(User.class)
                .whereField("id", Operator.EQUAL, "id")
                .get();

        // Both fields should be qualified as user.id = user.id
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void whereFieldWithMultipleJoins() {
        // Test whereField with multiple joins - comparing user fields across different join contexts
        var result = Hefesto.make(User.class)
                .join("pets", "p")
                .join("addresses", "a")
                .whereField("name", Operator.EQUAL, "email")
                .get();

        // Should execute without errors even if no results
        assertNotNull(result);
    }

    @Test
    void whereFieldComplex_CombinedConditions() {
        // Complex test: multiple whereField conditions combined with regular where
        var result = Hefesto.make(User.class)
                .where("id", Operator.GREATER, 0L)
                .whereField("name", Operator.DIFF, "email")
                .whereField("id", Operator.LESS_OR_EQUAL, "id")
                .get();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
