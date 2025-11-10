package io.github.robertomike.hql.hefesto.constructors;

import io.github.robertomike.hefesto.hql.builders.Hefesto;
import io.github.robertomike.hefesto.enums.SelectOperator;
import io.github.robertomike.hql.BaseTest;
import io.github.robertomike.hql.hefesto.models.User;
import io.github.robertomike.hql.hefesto.models.alias.PhotoAndCountName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(BaseTest.class)
public class GroupByBuilderTest {
    @Test
    void groupBy() {
        var result = new Hefesto<>(User.class, PhotoAndCountName.class)
                .addSelect("photo")
                .addSelect("name", "nameCount", SelectOperator.COUNT)
                .groupBy("photo")
                .get();

        assertFalse(result.isEmpty());
    }

    @Test
    void groupByMany() {
        var result = Hefesto.make(User.class)
                .addSelect("name")
                .groupBy("photo", "name")
                .get();

        assertFalse(result.isEmpty());
    }
}
