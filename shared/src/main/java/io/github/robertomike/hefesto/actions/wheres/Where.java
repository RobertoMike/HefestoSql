package io.github.robertomike.hefesto.actions.wheres;

import io.github.robertomike.hefesto.enums.Operator;
import io.github.robertomike.hefesto.enums.WhereOperator;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Where extends BaseWhere {
    private final String field;
    protected Operator operator = Operator.EQUAL;
    protected Object value;

    public Where(String field, Operator operator) {
        this.field = field;
        this.operator = operator;
    }
    public Where(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    public Where(String field, Operator operator, Object value, WhereOperator whereOperator) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.whereOperation = whereOperator;
    }

    public static Where make(String field, Object value, WhereOperator whereOperator) {
        return new Where(field, Operator.EQUAL, value, whereOperator);
    }

    public static Where make(String field, Operator operator, WhereOperator whereOperator) {
        return new Where(field, operator, null, whereOperator);
    }
}
