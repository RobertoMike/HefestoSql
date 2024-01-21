package io.github.robertomike.hefesto.actions;

import io.github.robertomike.hefesto.enums.SelectOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
