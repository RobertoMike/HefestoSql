package io.github.robertomike.hibernate6.hefesto.constructors;

import io.github.robertomike.hefesto.actions.wheres.Where;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.enums.WhereOperator;
import io.github.robertomike.hefesto.exceptions.QueryException;
import io.github.robertomike.hibernate6.BaseTest;
import io.github.robertomike.hibernate6.enums.Status;
import io.github.robertomike.hibernate6.hefesto.models.Address;
import io.github.robertomike.hibernate6.hefesto.models.User;
import io.github.robertomike.hibernate6.hefesto.models.UserPet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
