package io.github.robertomike.actions.wheres;

import io.github.robertomike.enums.WhereOperator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BaseWhere {
    protected WhereOperator whereOperation = WhereOperator.AND;
}
