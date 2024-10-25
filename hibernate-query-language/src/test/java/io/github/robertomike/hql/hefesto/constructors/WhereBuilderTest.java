package io.github.robertomike.hql.hefesto.constructors;

import io.github.robertomike.hql.BaseTest;
import io.github.robertomike.hefesto.actions.wheres.BaseWhere;
import io.github.robertomike.hefesto.actions.wheres.Where;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hql.enums.Status;
import io.github.robertomike.hefesto.exceptions.QueryException;
import io.github.robertomike.hefesto.exceptions.UnsupportedOperationException;
import io.github.robertomike.hql.hefesto.models.Pet;
import io.github.robertomike.hql.hefesto.models.User;
import io.github.robertomike.hql.hefesto.models.UserPet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BaseTest.class)
public class WhereBuilderTest {
    @Test
    void equal() {
        var result = Hefesto.make(User.class)
                .where("name", "test").findFirst();

        assertTrue(result.isPresent());
        assertNotNull(result.get());
        assertEquals("test@mail.com", result.get().getEmail());
    }

    @Test
    void like() {
        var result = Hefesto.make(User.class)
                .where("name", Operator.LIKE, "l%")
                .get();

        assertFalse(result.isEmpty());
    }

    @Test
    void notLike() {
        var result = Hefesto.make(User.class)
                .where("name", Operator.NOT_LIKE, "l%")
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.stream().noneMatch(u -> u.getName().startsWith("l")));
    }

    @Test
    void in() {
        var result = Hefesto.make(User.class)
                .whereIn("name", "test", "petto")
                .get();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void notIn() {
        var result = Hefesto.make(User.class)
                .whereNotIn("name", "test", "petto")
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.stream().noneMatch(u -> u.getName().equals("test")));
    }

    @Test
    void inIterable() {
        var result = Hefesto.make(User.class)
                .whereIn("name", Arrays.asList("test", "petto"))
                .get();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void notInIterable() {
        var result = Hefesto.make(User.class)
                .whereNotIn("name", Arrays.asList("test", "petto"))
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.stream().noneMatch(u -> u.getName().equals("test")));
    }

    @Test
    void diff() {
        var result = Hefesto.make(User.class)
                .where("name", Operator.DIFF, "test")
                .findFirst();

        assertTrue(result.isPresent());
        assertNotNull(result.get());
        assertNotEquals("test@mail.com", result.get().getEmail());
    }

    @Test
    void greater() {
        var result = Hefesto.make(User.class)
                .where("id", Operator.GREATER, 2L)
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.size() > 2);
    }

    @Test
    void less() {
        var result = Hefesto.make(User.class)
                .where("id", Operator.LESS, 2L)
                .get();

        assertFalse(result.isEmpty());
    }

    @Test
    void lessOrEquals() {
        var result = Hefesto.make(User.class)
                .where("id", Operator.LESS_OR_EQUAL, 2L)
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }

    @Test
    void greaterOrEquals() {
        var result = Hefesto.make(User.class)
                .where("id", Operator.GREATER_OR_EQUAL, 2L)
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.size() > 3);
    }

    @Test
    void isNull() {
        var result = Hefesto.make(User.class)
                .whereIsNull("photo")
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.size() > 3);
    }

    @Test
    void isNotNull() {
        var result = Hefesto.make(User.class)
                .whereIsNotNull("photo")
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.size() > 3);
    }

    @Test
    void isNullAndEquals() {
        var result = Hefesto.make(User.class)
                .whereIsNull("photo")
                .where("name", "test")
                .findFirst();

        assertFalse(result.isEmpty());
        assertEquals("test@mail.com", result.get().getEmail());
    }

    @Test
    void isNotNullAndEquals() {
        var result = Hefesto.make(User.class)
                .whereIsNotNull("photo")
                .where("name", "test")
                .findFirst();

        assertTrue(result.isEmpty());
    }

    @Test
    void whereExistAndEquals() {
        var subQuery = Hefesto.make(UserPet.class)
                .whereField("user.id", "id");

        var result = Hefesto.make(User.class)
                .whereExists(subQuery)
                .findFirstById(2L);

        assertFalse(result.isEmpty());
        assertEquals("petto@mail.com", result.get().getEmail());
    }

    @Test
    void orWhere() {
        var result = Hefesto.make(Pet.class)
                .where("name", "aaaaa")
                .orWhere("name", "lola")
                .findFirst();

        assertFalse(result.isEmpty());
    }

    @Test
    void orWhereList() {
        var result = Hefesto.make(Pet.class)
                .where("name", "aaaaa")
                .orWhere("name", "lola")
                .orWhere("name", "grillo")
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }

    @Test
    void whereWithJoinAlias() {
        var result = Hefesto.make(User.class)
                .join("pets")
                .where("pets.name", "lola")
                .findFirst();

        assertFalse(result.isEmpty());
        assertEquals("test", result.get().getName());
    }

    @Test
    void whereWithLeftJoin() {
        var result = Hefesto.make(User.class)
                .join("pets", JoinOperator.LEFT)
                .where("user.id", 4L)
                .findFirst();

        assertFalse(result.isEmpty());
        assertTrue(result.get().getPets().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(value = Operator.class, names = {"NOT_FIND_IN_SET", "FIND_IN_SET", "NOT_IN", "IN", "IS_NULL", "IS_NOT_NULL"})
    void whereFieldWithUnsupportedOperator(Operator operator) {
        var query = Hefesto.make(User.class)
                .join("pets", JoinOperator.LEFT)
                .whereField("id", operator, "pets.name");

        assertThrows(
                UnsupportedOperationException.class,
                query::findFirst
        );
    }

    @Test
    void invalidValueForWhereInOperator() {
        var query = Hefesto.make(User.class)
                .where(new Where("id", Operator.IN, ""));

        assertThrows(
                UnsupportedOperationException.class,
                query::findFirst
        );
    }

    @Test
    void findInSet() {
        var result = Hefesto.make(User.class)
                .where("status", Operator.FIND_IN_SET, Status.ACTIVE.name())
                .get();


        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }

    @Test
    void notFindInSet() {
        var result = Hefesto.make(User.class)
                .where("status", Operator.NOT_FIND_IN_SET, Status.ACTIVE.name())
                .get();


        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }

    @Test
    void whereRaw() {
        var result = Hefesto.make(User.class)
                .whereRaw("id = 1")
                .findFirst();


        assertFalse(result.isEmpty());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void orWhereRaw() {
        var result = Hefesto.make(User.class)
                .where("id", 2L)
                .orWhereRaw("id = 1")
                .get();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void passingUnsupportedWhere() {
        var query = Hefesto.make(User.class)
                .where(new BaseWhere() {
                });

        assertThrows(
                QueryException.class,
                query::get
        );
    }
}
