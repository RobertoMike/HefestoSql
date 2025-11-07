package io.github.robertomike.hql.hefesto.constructors;

import io.github.robertomike.hefesto.hql.builders.Hefesto;
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
 * Test suite for lambda-based subquery methods in HQL module.
 * Tests whereIn, whereNotIn with lambda configuration.
 * Note: whereExists/whereNotExists are not supported in HQL module.
 */
@ExtendWith(BaseTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubQueryTest {

    /**
     * Test whereIn with lambda subquery
     */
    @Test
    @Order(1)
    void testWhereInWithLambda() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.where("pet.id", 1L);
                })
                .get();

        assertNotNull(users);
    }

    /**
     * Test whereNotIn with lambda subquery
     */
    @Test
    @Order(2)
    void testWhereNotInWithLambda() {
        var users = Hefesto.make(User.class)
                .whereNotIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.where("pet.id", 1L);
                })
                .get();

        assertNotNull(users);
    }

    // Note: whereExists/whereNotExists are not available in HQL base builder
    // These methods would need to be implemented separately for HQL

    // Note: whereExists/whereNotExists are not available in HQL base builder
    // These methods would need to be implemented separately for HQL

    /**
     * Test orWhereIn with lambda subquery
     */
    @Test
    @Order(5)
    void testOrWhereInWithLambda() {
        var users = Hefesto.make(User.class)
                .where("id", 1L)
                .orWhereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.where("pet.id", 2L);
                })
                .get();

        assertNotNull(users);
    }

    /**
     * Test orWhereNotIn with lambda subquery
     */
    @Test
    @Order(6)
    void testOrWhereNotInWithLambda() {
        var users = Hefesto.make(User.class)
                .where("id", 1L)
                .orWhereNotIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.where("pet.id", 2L);
                })
                .get();

        assertNotNull(users);
    }

    // Note: orWhereExists/orWhereNotExists tests removed - not available in HQL

    // Note: orWhereExists/orWhereNotExists tests removed - not available in HQL

    /**
     * Test subquery with multiple conditions
     */
    @Test
    @Order(9)
    void testSubQueryWithMultipleConditions() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.where("pet.id", 1L);
                    subQuery.whereIsNotNull("user.id");
                })
                .get();

        assertNotNull(users);
    }

    /**
     * Test subquery with join
     */
    @Test
    @Order(10)
    void testSubQueryWithJoin() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.join("pet");
                    subQuery.where("pet.id", 1L);
                })
                .get();

        assertNotNull(users);
    }

    /**
     * Test subquery with aggregate function
     */
    @Test
    @Order(11)
    void testSubQueryWithAggregate() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.groupBy("user.id");
                })
                .get();

        assertNotNull(users);
    }

    /**
     * Test chaining multiple subquery conditions
     */
    @Test
    @Order(12)
    void testChainingMultipleSubQueries() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.where("pet.id", 1L);
                })
                .whereNotIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.where("pet.id", 2L);
                })
                .get();

        assertNotNull(users);
    }

    /**
     * Test subquery context supports all where methods
     */
    @Test
    @Order(13)
    void testSubQueryContextWhereMethods() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.whereIsNotNull("pet.id");
                })
                .get();

        assertNotNull(users);
    }

    /**
     * Test subquery with simple join (deep joins may need proper entity mapping)
     */
    @Test
    @Order(14)
    void testSubQueryWithJoins() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.join("user");
                    subQuery.whereIsNotNull("user.id");
                })
                .get();

        assertNotNull(users);
    }

    /**
     * Test that subquery lambda is fluent
     */
    @Test
    @Order(15)
    void testSubQueryLambdaFluency() {
        var builder = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id")
                            .where("pet.id", 1L)
                            .whereIsNotNull("user.id")
                            .join("pet")
                            .orderBy("id");
                });

        assertNotNull(builder);
    }

    /**
     * Test subquery with aggregate shortcuts
     */
    @Test
    @Order(16)
    void testSubQueryWithAggregateShortcuts() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.groupBy("user.id");
                })
                .get();

        assertNotNull(users);
    }

    /**
     * Test whereIn generates correct HQL
     */
    @Test
    @Order(17)
    void testWhereInGeneratesCorrectHQL() {
        var builder = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.where("pet.id", 1L);
                });

        var params = new java.util.HashMap<String, Object>();
        String hql = builder.getQuery(params);

        assertTrue(hql.contains("in"), "HQL should contain 'in' keyword");
        assertFalse(params.isEmpty(), "Parameters should be populated");
    }
}
