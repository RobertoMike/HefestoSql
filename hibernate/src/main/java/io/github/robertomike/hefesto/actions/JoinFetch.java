package io.github.robertomike.hefesto.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.persistence.criteria.JoinType;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class JoinFetch {
    private final String relationship;
    public String alias = null;
    public boolean nested;
    private JoinType joinType = JoinType.INNER;

    public static JoinFetch make(String relationship) {
        return new JoinFetch(relationship, relationship.replace(".", "_"), false, JoinType.INNER);
    }

    public static JoinFetch make(String relationship, JoinType joinType) {
        return new JoinFetch(relationship, relationship.replace(".", "_"), false, joinType);
    }

    public static JoinFetch make(String relationship, boolean nested) {
        return new JoinFetch(relationship, relationship.replace(".", "_"), nested, JoinType.INNER);
    }

    public static JoinFetch make(String relationship, String alias, boolean nested) {
        return new JoinFetch(relationship, alias, nested, JoinType.INNER);
    }

}
