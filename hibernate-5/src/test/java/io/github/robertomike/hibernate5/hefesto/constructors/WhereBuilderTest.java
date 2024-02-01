package io.github.robertomike.hibernate5.hefesto.constructors;

import io.github.robertomike.hefesto.actions.wheres.BaseWhere;
import io.github.robertomike.hefesto.actions.wheres.Where;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.JoinOperator;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.exceptions.QueryException;
import io.github.robertomike.hefesto.exceptions.UnsupportedOperationException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import io.github.robertomike.hibernate5.BaseTest;
import io.github.robertomike.hibernate5.enums.Status;
import io.github.robertomike.hibernate5.hefesto.models.Pet;
import io.github.robertomike.hibernate5.hefesto.models.User;
import io.github.robertomike.hibernate5.hefesto.models.UserPet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Map;

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
                .whereIn("name", new String[]{"test", "petto"})
                .get();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void notIn() {
        var result = Hefesto.make(User.class)
                .whereNotIn("name", new String[]{"test", "petto"})
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
                .where("id", Operator.GREATER, 2)
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.size() > 2);
    }

    @Test
    void less() {
        var result = Hefesto.make(User.class)
                .where("id", Operator.LESS, 2)
                .get();

        assertFalse(result.isEmpty());
    }

    @Test
    void lessOrEquals() {
        var result = Hefesto.make(User.class)
                .where("id", Operator.LESS_OR_EQUAL, 2)
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }

    @Test
    void greaterOrEquals() {
        var result = Hefesto.make(User.class)
                .where("id", Operator.GREATER_OR_EQUAL, 2)
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
                .join("pets", "Pet")
                .where("Pet.name", "lola")
                .findFirst();

        assertFalse(result.isEmpty());
        assertEquals("test", result.get().getName());
    }

    @Test
    void whereWithLeftJoin() {
        var result = Hefesto.make(User.class)
                .join("pets", JoinOperator.LEFT)
                .where("id", 4)
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
                .where("status", Operator.FIND_IN_SET, Status.ACTIVE)
                .get();


        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }

    @Test
    void notFindInSet() {
        var result = Hefesto.make(User.class)
                .where("status", Operator.NOT_FIND_IN_SET, Status.ACTIVE)
                .get();


        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }

    @Test
    void customWhere() {
        var result = Hefesto.make(User.class)
                .whereCustom((CriteriaBuilder cb, CriteriaQuery<?> cr, Root<?> root, Map<String, Join<?, ?>> joins, Root<?> parentRoot) -> cb.equal(root.get("id"), 1L))
                .findFirst();


        assertFalse(result.isEmpty());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void orCustomWhere() {
        var result = Hefesto.make(User.class)
                .where("id", 2)
                .orWhereCustom((CriteriaBuilder cb, CriteriaQuery<?> cr, Root<?> root, Map<String, Join<?, ?>> joins, Root<?> parentRoot) -> cb.equal(root.get("id"), 1L))
                .get();


        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void passingUnsupportedWhere() {
        var query = Hefesto.make(User.class)
                .where(new BaseWhere() {});

        assertThrows(
                QueryException.class,
                () -> query.get()
        );
    }
}
