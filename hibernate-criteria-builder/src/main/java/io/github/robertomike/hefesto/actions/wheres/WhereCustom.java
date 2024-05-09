package io.github.robertomike.hefesto.actions.wheres;

import io.github.robertomike.hefesto.enums.WhereOperator;
import javax.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class WhereCustom extends BaseWhere {
    private Custom custom;

    public WhereCustom(Custom custom, WhereOperator operator) {
        this.whereOperation = operator;
        this.custom = custom;
    }

    public interface Custom {
        @Nonnull
        Predicate call(@Nonnull CriteriaBuilder cb, @Nonnull CriteriaQuery<?> cr, @Nonnull Root<?> root, @Nonnull Map<String, Join<?, ?>> joins, @Nullable Root<?> parentRoot);
    }
}
