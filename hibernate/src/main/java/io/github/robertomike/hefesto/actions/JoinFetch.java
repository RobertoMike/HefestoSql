package io.github.robertomike.hefesto.actions;

import javax.persistence.criteria.JoinType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class JoinFetch {
    private final String relationship;
    private JoinType joinType = JoinType.INNER;

    public static JoinFetch make(String relationship) {
        return new JoinFetch(relationship);
    }
    public static JoinFetch make(String relationship, JoinType joinType) {
        return new JoinFetch(relationship, joinType);
    }
}
