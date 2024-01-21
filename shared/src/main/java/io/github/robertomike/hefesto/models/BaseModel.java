package io.github.robertomike.hefesto.models;

/**
 * This class is the base for the models that can be used inside HefestoSql
 */
public interface BaseModel {
    /**
     * Retrieves the table.
     *
     * @return  the table as a string
     */
    String getTable();
}
