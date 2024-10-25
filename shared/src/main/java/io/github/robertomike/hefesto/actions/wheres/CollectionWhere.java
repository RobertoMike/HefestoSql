package io.github.robertomike.hefesto.actions.wheres;

import io.github.robertomike.hefesto.enums.WhereOperator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * This is a collections of wheres that will be processed later
 */
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class CollectionWhere extends BaseWhere {
    /**
     * The wheres that will be used inside the parenthesis
     */
    private final List<? extends BaseWhere> wheres;

    /**
     *
     * @param wheres the wheres that will be used inside the parenthesis
     * @param whereOperation the operator that will be used
     */
    public CollectionWhere(List<? extends BaseWhere> wheres, WhereOperator whereOperation) {
        this.wheres = wheres;
        this.whereOperation = whereOperation;
    }
}
