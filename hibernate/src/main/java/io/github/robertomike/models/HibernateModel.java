package io.github.robertomike.models;

import jakarta.persistence.Table;

public interface HibernateModel extends BaseModel{
    default String getTable() {
        return getClass().getAnnotation(Table.class).name();
    }
}
