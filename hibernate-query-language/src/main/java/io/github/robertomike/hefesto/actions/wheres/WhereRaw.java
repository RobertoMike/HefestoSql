package io.github.robertomike.hefesto.actions.wheres;

import io.github.robertomike.hefesto.enums.WhereOperator;

public class WhereRaw extends BaseWhere {
    public String query;

    public WhereRaw(String query) {
        this.query = query;
        this.whereOperation = WhereOperator.AND;
    }

    public WhereRaw(String query, WhereOperator whereOperator) {
        this.query = query;
        this.whereOperation = whereOperator;
    }
}
