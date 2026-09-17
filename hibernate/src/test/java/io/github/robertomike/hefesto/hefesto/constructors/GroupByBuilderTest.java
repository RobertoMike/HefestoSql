package io.github.robertomike.hefesto.hefesto.constructors;

import io.github.robertomike.hefesto.BaseTest;
import io.github.robertomike.hefesto.builders.Hefesto;
import io.github.robertomike.hefesto.enums.SelectOperator;
import io.github.robertomike.hefesto.hefesto.models.User;
import io.github.robertomike.hefesto.hefesto.models.alias.PhotoAndCountName;
import io.github.robertomike.hefesto.hefesto.models.alias.PhotoAndName;
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
        var result = Hefesto.make(User.class, PhotoAndName.class)
                .addSelect("name")
                .groupBy("photo", "name")
                .get();

        assertFalse(result.isEmpty());
    }
}
