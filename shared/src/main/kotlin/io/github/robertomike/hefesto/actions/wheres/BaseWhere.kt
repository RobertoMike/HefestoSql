package io.github.robertomike.hefesto.actions.wheres

import io.github.robertomike.hefesto.enums.WhereOperator

/**
 * Base class for all WHERE conditions.
 * 
 * All WHERE clause types extend this class to support
 * logical operators (AND/OR) between conditions.
 */
abstract class BaseWhere {
    /**
     * The logical operator used to combine this condition with the previous one.
     * Defaults to AND.
     */
    open var whereOperation: WhereOperator = WhereOperator.AND
}
