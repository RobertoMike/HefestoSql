package io.github.robertomike.hefesto.hefesto.constructors;

import io.github.robertomike.hefesto.BaseTest;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.hefesto.models.User;
import io.github.robertomike.hefesto.hefesto.models.UserPet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BaseTest.class)
public class WhereFieldTest {

    @Test
    void whereFieldEqual_SameEntity() {
        // Test comparing two fields on the same entity
        // This would find users where name equals email (if any exist)
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
        // In a real scenario, this could be used for date comparisons
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
        // Test comparing fields across joined entities
        var result = Hefesto.make(User.class)
                .join("pets", "p")
                .whereField("name", Operator.EQUAL, "p.name")
                .get();

        // Should execute without errors
        assertNotNull(result);
    }

    @Test
    void whereFieldInSubQuery() {
        // Test whereField in a subquery context - correlating parent and subquery fields
        var result = Hefesto.make(User.class)
                .whereExists(UserPet.class, subQuery -> {
                    // Compare subquery's user.id with parent's id
                    subQuery.whereField("user.id", "id");
                })
                .get();

        // Should find users who have pets
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void whereFieldInSubQuery_WithOperator() {
        // Test whereField with operator in subquery
        var result = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    // Compare two fields in the subquery entity
                    subQuery.whereField("id", Operator.GREATER_OR_EQUAL, "id");
                })
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
        // Test whereField with join using alias
        var result = Hefesto.make(UserPet.class)
                .join("user", "u")
                .join("pet", "p")
                .whereField("u.name", Operator.DIFF, "p.name")
                .get();

        // Should find user-pet relationships where user name != pet name
        assertNotNull(result);
    }

    @Test
    void whereFieldComplex_JoinWithSubQuery() {
        // Complex test: whereField in main query with join, plus subquery with whereField
        var result = Hefesto.make(User.class)
                .join("pets", "p")
                .whereField("name", Operator.DIFF, "p.name")
                .whereExists(UserPet.class, subQuery -> {
                    subQuery.whereField("user.id", "id");
                    subQuery.where("pet.name", Operator.LIKE, "%o%");
                })
                .get();

        assertNotNull(result);
    }
}
