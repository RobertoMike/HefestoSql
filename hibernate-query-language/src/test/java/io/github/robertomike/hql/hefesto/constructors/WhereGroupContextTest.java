package io.github.robertomike.hql.hefesto.constructors;

import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.hql.builders.Hefesto;
import io.github.robertomike.hql.BaseTest;
import io.github.robertomike.hql.hefesto.models.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for WhereGroupContext (whereAny/whereAll).
 * Tests lambda-based conditional grouping with complex logic.
 */
@ExtendWith(BaseTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WhereGroupContextTest {

    @Test
    @Order(1)
    public void testWhereAnyWithMultipleConditions() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("name", "test");
                    group.where("email", "javi@mail.com");
                })
                .get();

        assertNotNull(users);
        assertTrue(users.size() >= 2, "Should find users matching either condition");
    }

    @Test
    @Order(2)
    public void testWhereAllWithMultipleConditions() {
        var users = Hefesto.make(User.class)
                .whereAll(group -> {
                    group.where("active", true);
                    group.whereIsNotNull("email");
                })
                .get();

        assertNotNull(users);
        assertTrue(users.size() > 0, "Should find active users with email");
    }

    @Test
    @Order(3)
    public void testWhereAnyWithOperators() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("id", Operator.GREATER, 5);
                    group.where("id", Operator.LESS, 3);
                })
                .get();

        assertNotNull(users);
        assertTrue(users.size() > 0);
    }

    @Test
    @Order(4)
    public void testWhereAnyWithIsNull() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIsNull("deletedAt");
                    group.where("active", false);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(5)
    public void testWhereAnyWithIsNotNull() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIsNotNull("email");
                    group.whereIsNotNull("phone");
                })
                .get();

        assertNotNull(users);
        assertTrue(users.size() > 0);
    }

    @Test
    @Order(6)
    public void testWhereAnyWithWhereIn() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIn("id", 1L, 2L, 3L);
                    group.where("name", "javi");
                })
                .get();

        assertNotNull(users);
        assertTrue(users.size() >= 3);
    }

    @Test
    @Order(7)
    public void testWhereAnyWithWhereInIterable() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIn("id", List.of(1L, 2L, 3L, 4L, 5L));
                    group.where("active", true);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(8)
    public void testWhereAnyWithWhereNotIn() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereNotIn("id", 999L, 1000L);
                    group.where("active", false);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(9)
    public void testWhereAnyWithWhereNotInIterable() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereNotIn("id", List.of(999L, 1000L));
                    group.where("name", "test");
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(10)
    public void testNestedWhereAnyInsideWhereAll() {
        var users = Hefesto.make(User.class)
                .whereAll(group -> {
                    group.where("active", true);
                    group.whereAny(nested -> {
                        nested.where("name", "test");
                        nested.where("email", "javi@mail.com");
                    });
                })
                .get();

        assertNotNull(users);
        // Should find: active=true AND (name='test' OR email='javi@mail.com')
    }

    @Test
    @Order(11)
    public void testNestedWhereAllInsideWhereAny() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereAll(nested -> {
                        nested.where("active", true);
                        nested.whereIsNotNull("email");
                    });
                    group.where("id", 1L);
                })
                .get();

        assertNotNull(users);
        // Should find: (active=true AND email IS NOT NULL) OR id=1
    }

    @Test
    @Order(12)
    public void testDeeplyNestedGroups() {
        var users = Hefesto.make(User.class)
                .whereAll(group -> {
                    group.where("active", true);
                    group.whereAny(level1 -> {
                        level1.whereAll(level2 -> {
                            level2.where("name", "test");
                            level2.whereIsNotNull("email");
                        });
                        level1.where("id", Operator.LESS, 10L);
                    });
                })
                .get();

        assertNotNull(users);
        // Complex nested logic
    }

    @Test
    @Order(13)
    public void testWhereAnyWithMixedOperators() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("id", Operator.EQUAL, 1);
                    group.where("id", Operator.GREATER, 5);
                    group.where("name", Operator.EQUAL, "test");
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(14)
    public void testWhereAnyMethodChaining() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("name", "test")
                         .where("email", "javi@mail.com")
                         .whereIsNotNull("phone")
                         .whereIn("id", 1L, 2L, 3L);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(15)
    public void testWhereAllMethodChaining() {
        var users = Hefesto.make(User.class)
                .whereAll(group -> {
                    group.where("active", true)
                         .whereIsNotNull("email")
                         .where("id", Operator.GREATER, 0);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(16)
    public void testOrWhereAny() {
        var users = Hefesto.make(User.class)
                .where("active", true)
                .orWhereAny(group -> {
                    group.where("name", "leo");
                    group.where("email", "lara@mail.com");
                })
                .get();

        assertNotNull(users);
        // Should find: active=true OR (name='leo' OR email='lara@mail.com')
    }

    @Test
    @Order(17)
    public void testOrWhereAll() {
        var users = Hefesto.make(User.class)
                .where("id", 1)
                .orWhereAll(group -> {
                    group.where("active", true);
                    group.whereIsNotNull("email");
                })
                .get();

        assertNotNull(users);
        // Should find: id=1 OR (active=true AND email IS NOT NULL)
    }

    @Test
    @Order(18)
    public void testMultipleWhereAnyGroups() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("id", 1);
                    group.where("id", 2);
                })
                .whereAny(group -> {
                    group.where("active", true);
                    group.where("active", false);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(19)
    public void testComplexRealWorldScenario() {
        var users = Hefesto.make(User.class)
                .whereAll(group -> {
                    group.where("active", true);
                    group.whereIsNotNull("email");
                    group.whereAny(status -> {
                        status.where("verified", true);
                        status.whereAll(admin -> {
                            admin.where("role", "ADMIN");
                            admin.where("level", Operator.GREATER_OR_EQUAL, 5);
                        });
                    });
                })
                .get();

        assertNotNull(users);
        // Find: active=true AND email IS NOT NULL AND (verified=true OR (role='ADMIN' AND level>=5))
    }

    @Test
    @Order(20)
    public void testEmptyWhereAnyGroup() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    // Empty group - should not break
                })
                .get();

        assertNotNull(users);
        // Should return all users
    }

    @Test
    @Order(21)
    public void testWhereAnyWithNullValues() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("name", null); // Should be ignored
                    group.where("active", true); // Should work
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(22)
    public void testWhereAllCombinedWithRegularWhere() {
        var users = Hefesto.make(User.class)
                .where("active", true)
                .whereAll(group -> {
                    group.whereIsNotNull("email");
                    group.where("verified", true);
                })
                .whereIsNull("deletedAt")
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(23)
    public void testWhereAnyWithSubquery() {
        var subQuery = Hefesto.make(io.github.robertomike.hql.hefesto.models.UserPet.class)
                .addSelect("user.id");

        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIn("id", subQuery);
                    group.where("name", "test");
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(24)
    public void testWhereAllWithSubquery() {
        var subQuery = Hefesto.make(io.github.robertomike.hql.hefesto.models.UserPet.class)
                .addSelect("user.id")
                .where("active", true);

        var users = Hefesto.make(User.class)
                .whereAll(group -> {
                    group.whereIn("id", subQuery);
                    group.where("active", true);
                })
                .get();

        assertNotNull(users);
    }

    @Test
    @Order(25)
    public void testMultipleNestedGroupsWithMixedLogic() {
        var users = Hefesto.make(User.class)
                .whereAll(mainGroup -> {
                    mainGroup.where("active", true);
                    
                    mainGroup.whereAny(emailOrPhone -> {
                        emailOrPhone.whereIsNotNull("email");
                        emailOrPhone.whereIsNotNull("phone");
                    });
                    
                    mainGroup.whereAny(statusCheck -> {
                        statusCheck.where("verified", true);
                        statusCheck.whereAll(adminCheck -> {
                            adminCheck.where("role", "ADMIN");
                            adminCheck.whereIn("level", 5, 10, 15);
                        });
                    });
                })
                .get();

        assertNotNull(users);
        // Complex business logic: 
        // active=true AND (email NOT NULL OR phone NOT NULL) AND (verified=true OR (role='ADMIN' AND level IN (5,10,15)))
    }
}
