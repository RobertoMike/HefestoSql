package io.github.robertomike.hefesto.actions

import io.github.robertomike.hefesto.enums.JoinOperator
import java.util.*

data class Join(
    val table: String,
    var alias: String? = null,
    var fieldJoin: String? = null,
    var fieldReference: String? = null,
    var joinOperator: JoinOperator = JoinOperator.INNER
) {
    fun getAcronym(): String = alias ?: table.lowercase(Locale.ROOT)

    companion object {
        @JvmStatic
        fun make(table: String) = Join(table)

        @JvmStatic
        fun make(table: String, fieldJoin: String) = Join(table, fieldJoin = fieldJoin)

        @JvmStatic
        fun make(table: String, operator: JoinOperator) = Join(table, joinOperator = operator)

        @JvmStatic
        fun make(table: String, fieldJoin: String, fieldReference: String) =
            Join(table, fieldJoin = fieldJoin, fieldReference = fieldReference)

        @JvmStatic
        fun make(table: String, fieldJoin: String, operator: JoinOperator) =
            Join(table, fieldJoin = fieldJoin, joinOperator = operator)

        @JvmStatic
        fun make(table: String, fieldJoin: String, fieldReference: String, operator: JoinOperator) =
            Join(table, fieldJoin = fieldJoin, fieldReference = fieldReference, joinOperator = operator)

        @JvmStatic
        fun make(table: String, fieldJoin: String, fieldReference: String, alias: String, operator: JoinOperator) =
            Join(table, alias, fieldJoin, fieldReference, operator)
    }
}
