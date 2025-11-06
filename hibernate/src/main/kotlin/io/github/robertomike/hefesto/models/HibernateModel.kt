package io.github.robertomike.hefesto.models

import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.*

interface HibernateModel : BaseModel {
    override fun getTable(): String {
        return javaClass.simpleName
    }

    fun getOriginalTable(): String {
        val table = javaClass.getAnnotation(Table::class.java)

        if (table != null) {
            return table.name
        }

        val entity = javaClass.getAnnotation(Entity::class.java)

        if (entity != null && entity.name.isNotEmpty()) {
            return entity.name
        }

        return getTable().lowercase(Locale.ROOT)
    }
}
