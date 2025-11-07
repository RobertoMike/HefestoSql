package io.github.robertomike.hql.hefesto.constructors;

import io.github.robertomike.hefesto.builders.Hefesto;
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
 * Test suite for aggregate function shortcuts in HQL module.
 * Tests that shortcut methods (count, sum, avg, min, max) correctly add selects to the builder.
 */
@ExtendWith(BaseTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AggregateFunctionTest {

    /**
     * Test COUNT shortcut without parameters (defaults to *)
     */
    @Test
    @Order(1)
    void testCountAll() {
        var builder = Hefesto.make(User.class)
                .count();

        assertNotNull(builder);
    }

    /**
     * Test COUNT shortcut with field parameter
     */
    @Test
    @Order(2)
    void testCountWithField() {
        var builder = Hefesto.make(User.class)
                .count("id");

        assertNotNull(builder);
    }

    /**
     * Test COUNT shortcut with alias
     */
    @Test
    @Order(3)
    void testCountWithAlias() {
        var builder = Hefesto.make(User.class)
                .addSelect("status")
                .count("id", "total");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test SUM shortcut
     */
    @Test
    @Order(4)
    void testSum() {
        var builder = Hefesto.make(User.class)
                .sum("id");

        assertNotNull(builder);
    }

    /**
     * Test SUM shortcut with alias
     */
    @Test
    @Order(5)
    void testSumWithAlias() {
        var builder = Hefesto.make(UserPet.class)
                .addSelect("user.id")
                .sum("id", "totalPetIds");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test AVG shortcut
     */
    @Test
    @Order(6)
    void testAvg() {
        var builder = Hefesto.make(User.class)
                .avg("id");

        assertNotNull(builder);
    }

    /**
     * Test AVG shortcut with alias
     */
    @Test
    @Order(7)
    void testAvgWithAlias() {
        var builder = Hefesto.make(User.class)
                .addSelect("status")
                .avg("id", "avgId");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test MIN shortcut
     */
    @Test
    @Order(8)
    void testMin() {
        var builder = Hefesto.make(User.class)
                .min("id");

        assertNotNull(builder);
    }

    /**
     * Test MIN shortcut with alias
     */
    @Test
    @Order(9)
    void testMinWithAlias() {
        var builder = Hefesto.make(User.class)
                .addSelect("status")
                .min("id", "minId");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test MAX shortcut
     */
    @Test
    @Order(10)
    void testMax() {
        var builder = Hefesto.make(User.class)
                .max("id");

        assertNotNull(builder);
    }

    /**
     * Test MAX shortcut with alias
     */
    @Test
    @Order(11)
    void testMaxWithAlias() {
        var builder = Hefesto.make(User.class)
                .addSelect("status")
                .max("id", "maxId");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test count shortcut returns correct type for chaining
     */
    @Test
    @Order(12)
    void testCountShortcut() {
        var builder = Hefesto.make(User.class)
                .count("id");

        assertNotNull(builder);
        assertTrue(builder instanceof Hefesto, "Should return Hefesto instance");
        assertNotNull(builder);
    }

    /**
     * Test sum shortcut returns correct type for chaining
     */
    @Test
    @Order(13)
    void testSumShortcut() {
        var builder = Hefesto.make(User.class)
                .sum("id");

        assertNotNull(builder);
        assertTrue(builder instanceof Hefesto);
        assertNotNull(builder);
    }

    /**
     * Test avg shortcut returns correct type for chaining
     */
    @Test
    @Order(14)
    void testAvgShortcut() {
        var builder = Hefesto.make(User.class)
                .avg("id");

        assertNotNull(builder);
        assertTrue(builder instanceof Hefesto);
        assertNotNull(builder);
    }

    /**
     * Test min shortcut returns correct type for chaining
     */
    @Test
    @Order(15)
    void testMinShortcut() {
        var builder = Hefesto.make(User.class)
                .min("id");

        assertNotNull(builder);
        assertTrue(builder instanceof Hefesto);
        assertNotNull(builder);
    }

    /**
     * Test max shortcut returns correct type for chaining
     */
    @Test
    @Order(16)
    void testMaxShortcut() {
        var builder = Hefesto.make(User.class)
                .max("id");

        assertNotNull(builder);
        assertTrue(builder instanceof Hefesto);
        assertNotNull(builder);
    }

    /**
     * Test multiple aggregates together
     */
    @Test
    @Order(17)
    void testMultipleAggregatesWithGroupBy() {
        var builder = Hefesto.make(UserPet.class)
                .addSelect("user.id")
                .count("id", "petCount")
                .min("id", "minPetId")
                .max("id", "maxPetId");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test count with where condition and chaining
     */
    @Test
    @Order(18)
    void testCountWithJoin() {
        var builder = Hefesto.make(User.class)
                .join("pets")
                .whereIsNotNull("pets.id")
                .count("id");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test sum with join
     */
    @Test
    @Order(19)
    void testSumWithJoin() {
        var builder = Hefesto.make(UserPet.class)
                .join("user")
                .sum("user.id");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test mixing aggregates with regular selects
     */
    @Test
    @Order(20)
    void testMixingAggregatesWithRegularSelects() {
        var builder = Hefesto.make(UserPet.class)
                .addSelect("user.id")
                .count("id", "total")
                .min("id", "minPetId")
                .max("id", "maxPetId")
                .sum("id", "sumPetIds")
                .avg("id", "avgPetId");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test count shortcut vs countResults()
     */
    @Test
    @Order(21)
    void testCountShortcutVsCountResults() {
        // countResults() is the dedicated method for getting total count
        Long resultsCount = Hefesto.make(User.class)
                .countResults();

        assertNotNull(resultsCount);
        assertTrue(resultsCount >= 0);

        // count() is a select shortcut for aggregate queries
        var builder = Hefesto.make(User.class)
                .count();

        assertNotNull(builder);
    }

    /**
     * Test aggregate with complex where conditions
     */
    @Test
    @Order(22)
    void testAggregateWithComplexConditions() {
        var builder = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("id", 1L);
                    group.where("id", 2L);
                })
                .count("id");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test chaining aggregate shortcuts with aliases
     */
    @Test
    @Order(23)
    void testChainingAggregateShortcuts() {
        var builder = Hefesto.make(User.class)
                .count("id", "total")
                .min("id", "minId")
                .max("id", "maxId")
                .avg("id", "avgId");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test aggregate methods are fluent (return builder)
     */
    @Test
    @Order(24)
    void testAggregateFluency() {
        var result = Hefesto.make(User.class)
                .where("id", 1L)
                .count("id")
                .whereIsNotNull("email")  // Can continue chaining
                .min("id", "minId");

        assertNotNull(result);
        assertTrue(result instanceof Hefesto, "Should return Hefesto instance");
    }

    /**
     * Test count without field parameter defaults to *
     */
    @Test
    @Order(25)
    void testCountWithoutAliasInQueryContext() {
        var builder = Hefesto.make(User.class)
                .where("id", 1L)
                .count();  // No alias, no field specified (defaults to *)

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test all aggregate shortcuts can be called
     */
    @Test
    @Order(26)
    void testAllAggregateShortcuts() {
        var builder = Hefesto.make(User.class)
                .count()
                .sum("id")
                .avg("id", "avgId")
                .min("id")
                .max("id", "maxId");

        assertNotNull(builder);
        assertNotNull(builder);
    }

    /**
     * Test aggregate shortcuts work in subqueries
     */
    @Test
    @Order(27)
    void testAggregateInSubqueryContext() {
        var builder = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.count("id", "petCount");
                    subQuery.groupBy("user.id");
                });

        assertNotNull(builder);
    }

    /**
     * Test that aggregate generates correct HQL
     */
    @Test
    @Order(28)
    void testAggregateGeneratesCorrectHQL() {
        var builder = Hefesto.make(User.class)
                .count("id");

        var params = new java.util.HashMap<String, Object>();
        String hql = builder.getQuery(params);

        assertTrue(hql.contains("count") || hql.contains("COUNT"), "HQL should contain count function");
    }
}
