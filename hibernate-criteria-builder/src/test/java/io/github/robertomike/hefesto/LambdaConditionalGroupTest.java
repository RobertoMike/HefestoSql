package io.github.robertomike.hefesto;

import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.enums.Status;
import io.github.robertomike.hefesto.hefesto.models.Pet;
import io.github.robertomike.hefesto.hefesto.models.User;
import io.github.robertomike.hefesto.hefesto.models.User_;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for lambda-based conditional groups (whereAny/whereAll)
 */
@ExtendWith(BaseTest.class)
public class LambdaConditionalGroupTest {

    /**
     * Test whereAny with simple OR conditions
     */
    @Test
    public void testWhereAnyWithSimpleConditions() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("name", "John Doe");
                    group.where("name", "Jane Doe");
                })
                .get();



        assertNotNull(users);
        // Should find users with name 'John Doe' OR 'Jane Doe'
    }

    /**
     * Test whereAny with different operators
     */
    @Test
    public void testWhereAnyWithOperators() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("id", Operator.GREATER, 5L);
                    group.where("id", Operator.LESS, 2L);
                })
                .get();



        assertNotNull(users);
        // Should find users with id > 5 OR id < 2
    }

    /**
     * Test whereAll with AND conditions
     */
    @Test
    public void testWhereAllWithSimpleConditions() {
        var users = Hefesto.make(User.class)
                .whereAll(group -> {
                    group.where("id", Operator.GREATER, 0L);
                    group.whereIsNotNull("name");
                })
                .get();



        assertNotNull(users);
        // Should find users with id > 0 AND name IS NOT NULL
    }

    /**
     * Test whereAny with whereIn
     */
    @Test
    public void testWhereAnyWithWhereIn() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIn("name", "John Doe", "Jane Doe", "Bob Smith");
                    group.where("email", Operator.LIKE, "%@example.com");
                })
                .get();



        assertNotNull(users);
    }

    /**
     * Test whereAny with whereIn using iterable
     */
    @Test
    public void testWhereAnyWithWhereInIterable() {
        List<String> names = Arrays.asList("John Doe", "Jane Doe", "Bob Smith");
        
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIn("name", names);
                    group.where("email", Operator.LIKE, "%@test.com");
                })
                .get();



        assertNotNull(users);
    }

    /**
     * Test whereAny with whereNotIn
     */
    @Test
    public void testWhereAnyWithWhereNotIn() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereNotIn("name", "Admin", "System");
                    group.where("id", Operator.LESS, 10L);
                })
                .get();



        assertNotNull(users);
    }

    /**
     * Test whereAny with whereIsNull
     */
    @Test
    public void testWhereAnyWithWhereIsNull() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIsNull("photo");
                    group.where("name", "John Doe");
                })
                .get();



        assertNotNull(users);
    }

    /**
     * Test whereAny with whereIsNotNull
     */
    @Test
    public void testWhereAnyWithWhereIsNotNull() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIsNotNull("email");
                    group.whereIsNotNull("photo");
                })
                .get();



        assertNotNull(users);
    }

    /**
     * Test orWhereAny
     */
    @Test
    public void testOrWhereAny() {
        var users = Hefesto.make(User.class)
                .where("id", Operator.GREATER, 100L)
                .orWhereAny(group -> {
                    group.where("name", "John Doe");
                    group.where("name", "Jane Doe");
                })
                .get();



        assertNotNull(users);
        // Should find: id > 100 OR (name='John Doe' OR name='Jane Doe')
    }

    /**
     * Test orWhereAll
     */
    @Test
    public void testOrWhereAll() {
        var users = Hefesto.make(User.class)
                .where("name", "Admin")
                .orWhereAll(group -> {
                    group.where("id", Operator.LESS, 10L);
                    group.whereIsNotNull("email");
                })
                .get();



        assertNotNull(users);
        // Should find: name='Admin' OR (id < 10 AND email IS NOT NULL)
    }

    /**
     * Test nested groups - whereAny inside whereAll
     */
    @Test
    public void testNestedWhereAnyInsideWhereAll() {
        var users = Hefesto.make(User.class)
                .whereAll(group -> {
                    group.whereIsNotNull("email");
                    group.whereAny(nestedGroup -> {
                        nestedGroup.where("name", "John Doe");
                        nestedGroup.where("name", "Jane Doe");
                    });
                })
                .get();



        assertNotNull(users);
        // Should find: email IS NOT NULL AND (name='John Doe' OR name='Jane Doe')
    }

    /**
     * Test nested groups - whereAll inside whereAny
     */
    @Test
    public void testNestedWhereAllInsideWhereAny() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("name", "Admin");
                    group.whereAll(nestedGroup -> {
                        nestedGroup.where("id", Operator.GREATER, 5L);
                        nestedGroup.whereIsNotNull("photo");
                    });
                })
                .get();



        assertNotNull(users);
        // Should find: name='Admin' OR (id > 5 AND photo IS NOT NULL)
    }

    /**
     * Test complex nested groups - multiple levels
     */
    @Test
    public void testComplexNestedGroups() {
        var users = Hefesto.make(User.class)
                .whereAll(group -> {
                    group.whereIsNotNull("email");
                    group.whereAny(level1 -> {
                        level1.where("name", "John Doe");
                        level1.whereAll(level2 -> {
                            level2.where("id", Operator.GREATER, 10L);
                            level2.whereIsNotNull("photo");
                        });
                    });
                })
                .get();



        assertNotNull(users);
        // Should find: email IS NOT NULL AND (name='John Doe' OR (id > 10 AND photo IS NOT NULL))
    }

    /**
     * Test whereAny with type-safe JPA Metamodel attributes
     */
    @Test
    public void testWhereAnyWithMetamodel() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where(User_.name, "John Doe");
                    group.where(User_.name, "Jane Doe");
                })
                .get();



        assertNotNull(users);
    }

    /**
     * Test whereAny with metamodel and operators
     */
    @Test
    public void testWhereAnyWithMetamodelAndOperators() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where(User_.id, Operator.GREATER, 5L);
                    group.where(User_.id, Operator.LESS, 2L);
                })
                .get();



        assertNotNull(users);
    }

    /**
     * Test whereAny with metamodel whereIn
     */
    @Test
    public void testWhereAnyWithMetamodelWhereIn() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIn(User_.name, "John Doe", "Jane Doe", "Bob Smith");
                    group.where(User_.email, Operator.LIKE, "%@example.com");
                })
                .get();



        assertNotNull(users);
    }

    /**
     * Test whereAny with metamodel whereNotIn
     */
    @Test
    public void testWhereAnyWithMetamodelWhereNotIn() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereNotIn(User_.name, "Admin", "System");
                    group.where(User_.id, Operator.LESS, 10L);
                })
                .get();



        assertNotNull(users);
    }

    /**
     * Test whereAny with metamodel whereIsNull
     */
    @Test
    public void testWhereAnyWithMetamodelWhereIsNull() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIsNull(User_.photo);
                    group.where(User_.name, "John Doe");
                })
                .get();



        assertNotNull(users);
    }

    /**
     * Test whereAny with metamodel whereIsNotNull
     */
    @Test
    public void testWhereAnyWithMetamodelWhereIsNotNull() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.whereIsNotNull(User_.email);
                    group.whereIsNotNull(User_.photo);
                })
                .get();



        assertNotNull(users);
    }

    /**
     * Test mixing regular where with whereAny
     */
    @Test
    public void testMixingWhereWithWhereAny() {
        var users = Hefesto.make(User.class)
                .where("id", Operator.GREATER, 0L)
                .whereAny(group -> {
                    group.where("name", "John Doe");
                    group.where("name", "Jane Doe");
                })
                .whereIsNotNull("email")
                .get();



        assertNotNull(users);
        // Should find: id > 0 AND (name='John Doe' OR name='Jane Doe') AND email IS NOT NULL
    }

    /**
     * Test empty whereAny block (should be ignored)
     */
    @Test
    public void testEmptyWhereAnyBlock() {
        var users = Hefesto.make(User.class)
                .where("id", Operator.GREATER, 0L)
                .whereAny(group -> {
                    // Empty block
                })
                .whereIsNotNull("email")
                .get();



        assertNotNull(users);
        // Should find: id > 0 AND email IS NOT NULL (empty group ignored)
    }

    /**
     * Test whereAny with null values (should be ignored)
     */
    @Test
    public void testWhereAnyWithNullValues() {
        var users = Hefesto.make(User.class)
                .whereAny(group -> {
                    group.where("name", (String) null);  // Should be ignored
                    group.where("email", "test@example.com");
                })
                .get();



        assertNotNull(users);
        // Should only apply email condition
    }
}
