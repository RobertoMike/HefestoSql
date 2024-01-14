package io.github.robertomike.actions;


import io.github.robertomike.enums.JoinOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Join {
    private final String table;
    private String fieldJoin;
    private String fieldReference;
    private JoinOperator joinOperator = JoinOperator.INNER;

    public String getAcronym() {
        return table.toLowerCase(Locale.ROOT);
    }

    public static Join make(String table) {
        return new Join(table);
    }

    public static Join make(String table, String fieldJoin) {
        return new Join(table, fieldJoin, null);
    }
    public static Join make(String table, JoinOperator operator) {
        return new Join(table, null, null, operator);
    }

    public Join(String table, String fieldJoin, String fieldReference) {
        this.table = table;
        this.fieldJoin = fieldJoin;
        this.fieldReference = fieldReference;
    }

    public static Join make(String table, String fieldJoin, JoinOperator operator) {
        return new Join(table, fieldJoin, null, operator);
    }

    public static Join make(String table, String fieldJoin, String fieldReference) {
        return new Join(table, fieldJoin, fieldReference);
    }

    public static Join make(String table, String fieldJoin, String fieldReference, JoinOperator operator) {
        return new Join(table, fieldJoin, fieldReference, operator);
    }
}
