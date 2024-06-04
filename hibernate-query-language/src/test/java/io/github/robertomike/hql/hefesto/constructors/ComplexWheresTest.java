package io.github.robertomike.hql.hefesto.constructors;

import io.github.robertomike.hefesto.actions.wheres.WhereRaw;
import io.github.robertomike.hql.BaseTest;
import io.github.robertomike.hefesto.actions.wheres.Where;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hql.enums.Status;
import io.github.robertomike.hefesto.enums.WhereOperator;
import io.github.robertomike.hefesto.exceptions.QueryException;
import io.github.robertomike.hql.hefesto.models.Address;
import io.github.robertomike.hql.hefesto.models.User;
import io.github.robertomike.hql.hefesto.models.UserPet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BaseTest.class)
public class ComplexWheresTest {
    @Test
    void collectionsWhere() {
        var hefesto = Hefesto.make(User.class);

        hefesto.where("id", Operator.GREATER, 1L)
                .where(
                        Where.make("name", "petto", WhereOperator.OR),
                        Where.make("name", "mary", WhereOperator.OR),
                        Where.make("photo", Operator.IS_NOT_NULL, WhereOperator.OR)
                );

        var result = hefesto.get();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.size() > 3);
    }

    @Test
    void notExistsAndExists() {
        var hefesto = Hefesto.make(User.class);
        hefesto.setAcronymTable("u");

        hefesto.whereExists(
                Hefesto.make(UserPet.class)
                        .whereField("u.id", "user.id")
        ).whereNotExists(
                Hefesto.make(Address.class)
                        .whereField("u.id", "user.id")
        ).where("id", Operator.GREATER, 1L);

        var result = hefesto.get();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }

    @Test
    void subQueryInsideAWhereInOperator() {
        var results = Hefesto.make(User.class)
                .whereIn("id", Hefesto.make(UserPet.class)
                        .addSelect("user.id")
                )
                .get();


        assertFalse(results.isEmpty());
        assertTrue(results.size() > 1);
    }

    @Test
    void subQueryInsideAWhereInOperatorWithoutCustomResult() {
        var query = Hefesto.make(User.class)
                .whereIn("id", Hefesto.make(UserPet.class)
                        .addSelect("user.id")
                );


        assertNotNull(query.findFirst());
    }

    @Test
    void findInSetWithAnd() {
        var result = Hefesto.make(User.class)
                .where("status", Operator.FIND_IN_SET, Status.ACTIVE.name())
                .where("status", Operator.FIND_IN_SET, Status.BLOCKED.name())
                .get();

        assertFalse(result.isEmpty());
        assertTrue(result.size() == 1);
    }

    @Test
    void findInSetWithOr() {
        var result = Hefesto.make(User.class)
                .where("status", Operator.FIND_IN_SET, Status.ACTIVE.name())
                .orWhere("status", Operator.FIND_IN_SET, Status.PENDING.name())
                .get();


        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }

    @Test
    void whereRaw() {
        var result = Hefesto.make(User.class)
                .whereRaw("email = 'test@mail.com'")
                .whereRaw(new WhereRaw("email = 'petto@mail.com'", WhereOperator.OR))
                .get();


        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }
}
