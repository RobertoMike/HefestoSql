package io.github.robertomike.hefesto.constructors

import io.github.robertomike.hefesto.actions.Order

class ConstructOrderImplementation : ConstructOrder() {
    fun construct(): String {
        val ordersQuery = StringBuilder()

        items.forEach { value ->
            if (ordersQuery.isNotEmpty()) {
                ordersQuery.append(", ")
            }

            ordersQuery.append(apply(value))
        }

        if (items.isNotEmpty()) {
            ordersQuery.insert(0, "order by ")
        }

        return ordersQuery.toString()
    }

    fun apply(value: Order): String {
        return "${value.field} ${value.sort.name}"
    }
}
