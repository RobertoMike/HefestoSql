package io.github.robertomike.hefesto.constructors

class ConstructGroupByImplementation : ConstructGroupBy() {
    /**
     * Constructs a GROUP BY clause based on the items in the list.
     *
     * @return a String representing the GROUP BY clause, or an empty string if the `items` list is empty.
     */
    fun construct(): String {
        if (items.isEmpty()) {
            return ""
        }

        val groupBy = StringBuilder()

        items.forEach { value ->
            if (groupBy.isNotEmpty()) {
                groupBy.append(", ")
            }

            groupBy.append(value.field)
        }

        return "group by $groupBy"
    }
}
