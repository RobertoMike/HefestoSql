package io.github.robertomike.hefesto.actions

import jakarta.persistence.criteria.JoinType

data class JoinFetch(
    val relationship: String,
    var alias: String? = null,
    var nested: Boolean = false,
    var joinType: JoinType = JoinType.INNER
) {
    companion object {
        @JvmStatic
        fun make(relationship: String): JoinFetch {
            return JoinFetch(relationship, relationship.replace(".", "_"), false, JoinType.INNER)
        }

        @JvmStatic
        fun make(relationship: String, joinType: JoinType): JoinFetch {
            return JoinFetch(relationship, relationship.replace(".", "_"), false, joinType)
        }

        @JvmStatic
        fun make(relationship: String, nested: Boolean): JoinFetch {
            return JoinFetch(relationship, relationship.replace(".", "_"), nested, JoinType.INNER)
        }

        @JvmStatic
        fun make(relationship: String, alias: String, nested: Boolean): JoinFetch {
            return JoinFetch(relationship, alias, nested, JoinType.INNER)
        }
    }
}
