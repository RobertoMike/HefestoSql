package io.github.robertomike.hefesto.hefesto.constructors;

import io.github.robertomike.hefesto.BaseTest;
import io.github.robertomike.hefesto.actions.wheres.Where;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.enums.Status;
import io.github.robertomike.hefesto.enums.WhereOperator;
import io.github.robertomike.hefesto.exceptions.QueryException;
import io.github.robertomike.hefesto.hefesto.models.Address;
import io.github.robertomike.hefesto.hefesto.models.User;
import io.github.robertomike.hefesto.hefesto.models.UserPet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(BaseTest.class)
public class ComplexWheresTest {
    @Test
    void collectionsWhere() {
        var hefesto = Hefesto.make(User.class);

        hefesto.where("id", Operator.GREATER, 1)
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

        hefesto.whereExists(
                Hefesto.make(UserPet.class)
                        .whereField("user.id", "id")
        ).whereNotExists(
                Hefesto.make(Address.class)
                        .whereField("user.id", "id")
        ).where("id", Operator.GREATER, 1);

        var result = hefesto.get();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }

    @Test
    void invalidValueQuantityOfSelectForSubQueryInsideAWhereInOperator() {
        var query = Hefesto.make(User.class)
                .whereIn("id", Hefesto.make(UserPet.class));

        assertThrows(
                QueryException.class,
                query::findFirst
        );
    }

    @Test
    void subQueryInsideAWhereInOperator() {
        var results = Hefesto.make(User.class)
                .whereIn("id", Hefesto.make(UserPet.class)
                        .addSelect("user.id")
                        .setCustomResultForSubQuery(Long.class)
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


        assertThrows(
                QueryException.class,
                query::findFirst
        );
    }

    @Test
    void findInSetWithAnd() {
        var result = Hefesto.make(User.class)
                .where("status", Operator.FIND_IN_SET, Status.ACTIVE)
                .where("status", Operator.FIND_IN_SET, Status.BLOCKED)
                .get();


        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findInSetInsideCollectionWithOtherConditionAndPagination() {
        var result = Hefesto.make(User.class)
                .where("id", Operator.GREATER, 0)
                .where(Arrays.asList(
                        new Where("status", Operator.FIND_IN_SET, Status.PENDING, WhereOperator.OR),
                        new Where("status", Operator.FIND_IN_SET, Status.BLOCKED, WhereOperator.OR)
                ))
                .where("status", Operator.FIND_IN_SET, Status.INACTIVE)
                .page(3, 0);


        assertFalse(result.getData().isEmpty());
        assertEquals(1, result.getData().size());
    }

    @Test
    void findInSetInsideCollection() {
        var result = Hefesto.make(User.class)
                .where(Arrays.asList(
                        new Where("status", Operator.FIND_IN_SET, Status.ACTIVE),
                        new Where("status", Operator.FIND_IN_SET, Status.BLOCKED)
                ))
                .get();


        assertFalse(result.isEmpty());
        assertTrue(result.size() == 1);
    }

    @Test
    void findInSetWithOr() {
        var result = Hefesto.make(User.class)
                .where("status", Operator.FIND_IN_SET, Status.ACTIVE)
                .orWhere("status", Operator.FIND_IN_SET, Status.PENDING)
                .get();


        assertFalse(result.isEmpty());
        assertTrue(result.size() > 1);
    }
}
