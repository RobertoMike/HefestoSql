package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.enums.WhereOperator

/**
 * This is a collections of wheres that will be processed later
 */
data class CollectionWhere(
    /**
     * The wheres that will be used inside the parenthesis
     */
    val wheres: List<out BaseWhere>
) : BaseWhere() {

    /**
     * @param wheres the wheres that will be used inside the parenthesis
     * @param whereOperation the operator that will be used
     */
    constructor(wheres: List<out BaseWhere>, whereOperation: WhereOperator) : this(wheres) {
        this.whereOperation = whereOperation
    }
}
