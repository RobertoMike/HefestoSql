package io.github.robertomike.hql.hefesto.constructors;

import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.enums.SelectOperator;
import io.github.robertomike.hefesto.hql.builders.Hefesto;
import io.github.robertomike.hql.BaseTest;
import io.github.robertomike.hql.hefesto.models.User;
import io.github.robertomike.hql.hefesto.models.UserPet;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for SubQueryContext in HQL module.
 * Tests lambda-based subquery building with all available methods.
 */
@ExtendWith(BaseTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubQueryContextTest {

    @Test
    @Order(1)
    public void testSubQueryWithAddSelect() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.where("active", true);
                })
                .get();

        assertNotNull(users);
        assertTrue(users.size() > 0, "Should find users with active pets");
    }

    @Test
    @Order(2)
    public void testSubQueryWithAddSelectAndAlias() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id", "userId");
                    subQuery.where("active", true);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(3)
    public void testSubQueryWithSelectOperator() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id", SelectOperator.COUNT);
                    subQuery.where("active", true);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(4)
    public void testSubQueryWithSelectOperatorAndAlias() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id", "userId", SelectOperator.COUNT);
                    subQuery.where("active", true);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(5)
    public void testSubQueryWithWhereIsNull() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.whereIsNull("deletedAt");
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(6)
    public void testSubQueryWithWhereIsNotNull() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.whereIsNotNull("createdAt");
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(7)
    public void testSubQueryWithWhereInVarargs() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.whereIn("active", true, false);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(8)
    public void testSubQueryWithWhereInIterable() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.whereIn("active", List.of(true, false));
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(9)
    public void testSubQueryWithWhereNotInVarargs() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.whereNotIn("active", false);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(10)
    public void testSubQueryWithWhereNotInIterable() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.whereNotIn("active", List.of(false));
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(11)
    public void testSubQueryWithWhereRaw() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("userpet.user.id");
                    subQuery.whereRaw("active = true");
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(12)
    public void testSubQueryWithJoin() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.join("user");
                    subQuery.where("active", true);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(13)
    public void testSubQueryWithGroupBy() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.where("active", true);
                    subQuery.groupBy("user.id");
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(14)
    public void testSubQueryWithMultipleGroupBy() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("userpet.user.id", "userId");
                    subQuery.where("active", true);
                    subQuery.groupBy("userId", "active");
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(15)
    public void testSubQueryWithOrderBy() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("userpet.user.id");
                    subQuery.where("active", true);
                    subQuery.orderBy("createdAt");
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(16)
    public void testSubQueryMethodChaining() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery
                            .addSelect("userpet.user.id")
                            .where("active", true)
                            .whereIsNotNull("userpet.createdAt")
                            .orderBy("userpet.createdAt");
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(17)
    public void testSubQueryGetBuilder() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    var builder = subQuery.getBuilder();
                    assertNotNull(builder);
                    assertTrue(builder instanceof Hefesto);
                    
                    // Use builder for more advanced operations
                    builder.addSelect("user.id");
                    builder.where("active", true);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(18)
    public void testComplexSubQueryWithMultipleConditions() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery
                            .addSelect("userpet.user.id", "userId")
                            .where("userpet.active", true)
                            .whereIsNotNull("userpet.createdAt")
                            .whereIn("type", "DOG", "CAT")
                            .join("user")
                            .groupBy("userpet.user.id")
                            .orderBy("userpet.createdAt");
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(19)
    public void testSubQueryWithTypeFilter() {
        var users = Hefesto.make(User.class)
                .whereIn("id", UserPet.class, subQuery -> {
                    subQuery.addSelect("user.id");
                    subQuery.where("active", true);
                    subQuery.whereIn("type", "DOG", "CAT");
                })
                .get();

        assertNotNull(users);
    }
}
