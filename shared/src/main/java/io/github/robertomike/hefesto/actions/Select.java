package io.github.robertomike.hefesto.actions;

import io.github.robertomike.hefesto.enums.SelectOperator;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class Select {
    private final String field;
    private String alias;
    private SelectOperator operator;

    public Select(String field, String alias) {
        this.field = field;
        this.alias = alias;
    }

    public Select(String field, SelectOperator operator) {
        this.field = field;
        this.operator = operator;
    }
}
