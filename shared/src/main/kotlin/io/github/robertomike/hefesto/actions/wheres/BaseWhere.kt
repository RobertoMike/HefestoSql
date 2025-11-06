package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.enums.WhereOperator

/**
 * The base class for all the where
 */
abstract class BaseWhere {
    /**
     * The operator that will be used
     */
    open var whereOperation: WhereOperator = WhereOperator.AND
}
