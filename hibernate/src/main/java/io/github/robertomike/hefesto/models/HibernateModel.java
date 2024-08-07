package io.github.robertomike.hefesto.models;

import jakarta.persistence.Table;

public interface HibernateModel extends BaseModel {
    default String getTable() {
        return getClass().getSimpleName();
    }
    default String getOriginalTable() {
        return getClass().getAnnotation(Table.class).name();
    }
}
