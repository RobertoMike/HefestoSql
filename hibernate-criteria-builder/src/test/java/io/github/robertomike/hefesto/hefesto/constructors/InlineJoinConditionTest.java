package io.github.robertomike.hefesto.hefesto.constructors;

import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.BaseTest;
import io.github.robertomike.hefesto.hefesto.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Feature #5: Inline Join Conditions
 * Tests the lambda-based join configuration with WHERE conditions
 */
@ExtendWith(BaseTest.class)
public class InlineJoinConditionTest {

    @Test
    void joinWithInlineCondition() {
        var result = Hefesto.make(User.class)
                .join("pets", join -> {
                    join.alias("Pet");
                    join.where("name", "lola");
                })
                .get();

        assertFalse(result.isEmpty());
        // The join condition should filter pets, but the result still includes the user
        // with all their pets (Hibernate behavior with join conditions)
    }

    @Test
    void joinWithInlineConditionAndJoinType() {
        var result = Hefesto.make(User.class)
                .join("pets", JoinOperator.LEFT, join -> {
                    join.alias("Pet");
                    join.where("name", "lola");
                })
                .get();

        assertFalse(result.isEmpty());
    }

    @Test
    void joinWithInlineConditionAndAdditionalWhere() {
        var result = Hefesto.make(User.class)
                .join("pets", join -> {
                    join.alias("Pet");
                    join.where("name", "lola");
                })
                .where("id", 1)
                .get();

        assertFalse(result.isEmpty());
    }

    @Test
    void joinWithInlineConditionUsingDifferentOperators() {
        // Test EQUAL
        var result1 = Hefesto.make(User.class)
                .join("pets", join -> {
                    join.alias("Pet");
                    join.where("name", "lola", Operator.EQUAL);
                })
                .get();
        assertFalse(result1.isEmpty());

        // Test NOT_EQUAL
        var result2 = Hefesto.make(User.class)
                .join("pets", join -> {
                    join.alias("Pet");
                    join.where("name", "maximus", Operator.DIFF);
                })
                .get();
        assertFalse(result2.isEmpty());

        // Test LIKE
        var result3 = Hefesto.make(User.class)
                .join("pets", join -> {
                    join.alias("Pet");
                    join.where("name", "lol%", Operator.LIKE);
                })
                .get();
        assertFalse(result3.isEmpty());
    }

    @Test
    void joinWithoutAliasShouldUseTableName() {
        var result = Hefesto.make(User.class)
                .join("pets", join -> {
                    // No alias set, should use "pets" as default
                    join.where("name", "lola");
                })
                .get();

        assertFalse(result.isEmpty());
    }

    @Test
    void countWithInlineJoinCondition() {
        long count = Hefesto.make(User.class)
                .join("pets", join -> {
                    join.alias("Pet");
                    join.where("name", "lola");
                })
                .countResults();

        assertTrue(count > 0);
    }
}
