package io.github.robertomike.constructors;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ConstructJoinImplementation<T> extends ConstructJoin {
    private final Map<String, Join<?, ?>> joins = new HashMap<>();

    public void construct(Root<?> root) {
        items.forEach(value -> {
            JoinType joinType = null;

            switch (value.getJoinOperator()) {
                case INNER -> joinType = JoinType.INNER;
                case LEFT -> joinType = JoinType.LEFT;
                case RIGHT -> joinType = JoinType.RIGHT;
            }

            var name = value.getTable();

            var hasAlias = value.getFieldJoin() != null;

            if (hasAlias) {
                name = value.getFieldJoin();
            }

            Join<T, ?> join = root.join(value.getTable(), joinType);

            joins.put(name, join);
        });
    }
}
