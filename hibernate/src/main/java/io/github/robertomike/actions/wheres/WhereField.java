package io.github.robertomike.actions.wheres;

import io.github.robertomike.enums.Operator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WhereField extends Where {
    private String parentField;

    public WhereField(String field, String parentField) {
        super(field);
        this.parentField = parentField;
    }

    public WhereField(String field, Operator operator, String parentField) {
        super(field);
        this.operator = operator;
        this.parentField = parentField;
    }
}
