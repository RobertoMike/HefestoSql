package io.github.robertomike.hefesto.actions.wheres;

import io.github.robertomike.hefesto.enums.WhereOperator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The base class for all the where
 */
@Getter
@Setter
@ToString
public abstract class BaseWhere {
    /**
     * The operator that will be used
     */
    protected WhereOperator whereOperation = WhereOperator.AND;
}
