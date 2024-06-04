package io.github.robertomike.hefesto.constructors;

import lombok.Getter;

@Getter
public class ConstructGroupByImplementation extends ConstructGroupBy {
    /**
     * Constructs a GROUP BY clause based on the items in the list.
     *
     * @return a String representing the GROUP BY clause, or an empty string if the `items` list is empty.
     */
    public String construct() {
        if (items.isEmpty()) {
            return "";
        }

        StringBuilder groupBy = new StringBuilder();

        items.forEach((value) -> {
            if (!groupBy.isEmpty()) {
                groupBy.append(", ");
            }

            groupBy.append(value.field());
        });

        return "group by " + groupBy;
    }
}
