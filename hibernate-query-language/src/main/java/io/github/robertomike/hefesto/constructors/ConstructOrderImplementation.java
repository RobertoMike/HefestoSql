package io.github.robertomike.hefesto.constructors;

import io.github.robertomike.hefesto.actions.Order;

public class ConstructOrderImplementation extends ConstructOrder {
    public String construct() {
        StringBuilder ordersQuery = new StringBuilder();

        items.forEach((value) -> {
            if (!ordersQuery.isEmpty()) {
                ordersQuery.append(", ");
            }

            ordersQuery.append(apply(value));
        });

        if (!items.isEmpty()) {
            ordersQuery.insert(0, "order by ");
        }

        return ordersQuery.toString();
    }

    public String apply(Order value) {
        return value.getField() + " " + value.getSort().name();
    }
}
