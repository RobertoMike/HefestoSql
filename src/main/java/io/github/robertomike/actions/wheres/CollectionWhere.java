package io.github.robertomike.actions.wheres;

import io.github.robertomike.enums.WhereOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CollectionWhere extends BaseWhere {
    private final List<BaseWhere> wheres;

    public CollectionWhere(List<BaseWhere> wheres, WhereOperator whereOperation) {
        this.wheres = wheres;
        this.whereOperation = whereOperation;
    }
}
