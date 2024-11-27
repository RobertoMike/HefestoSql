package io.github.robertomike.hefesto.models;

import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.Locale;

public interface HibernateModel extends BaseModel {
    default String getTable() {
        return getClass().getSimpleName();
    }

    default String getOriginalTable() {
        Table table = getClass().getAnnotation(Table.class);

        if (table != null) {
            return table.name();
        }

        Entity entity = getClass().getAnnotation(Entity.class);

        if (entity != null && !entity.name().isEmpty()) {
            return entity.name();
        }

        return getTable().toLowerCase(Locale.ROOT);
    }
}
