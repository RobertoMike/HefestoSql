package io.github.robertomike.hefesto.actions.wheres;

import io.github.robertomike.hefesto.builders.BaseBuilder;
import io.github.robertomike.hefesto.enums.WhereOperator;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class WhereExist extends BaseWhere {
    private boolean exists = true;
    private final BaseBuilder subQuery;

    public WhereExist(BaseBuilder subQuery, WhereOperator whereOperator) {
        this.whereOperation = whereOperator;
        this.subQuery = subQuery;
    }

    public WhereExist(boolean exists, BaseBuilder subQuery, WhereOperator whereOperator) {
        this.exists = exists;
        this.whereOperation = whereOperator;
        this.subQuery = subQuery;
    }
}
