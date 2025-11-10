package io.github.robertomike.hefesto.actions

import jakarta.persistence.criteria.JoinType

/**
 * Represents a JOIN FETCH clause for eagerly loading relationships.
 * 
 * JOIN FETCH is used in JPA to eagerly load associated entities
 * and avoid N+1 query problems. It fetches the related data in
 * the same query rather than lazily loading it later.
 * 
 * @property relationship the relationship field to fetch (e.g., "address", "user.orders")
 * @property alias optional alias for the fetched relationship
 * @property nested true if this is a nested/deep fetch (e.g., "user.address.city")
 * @property joinType the type of join (INNER, LEFT, RIGHT), defaults to INNER
 * 
 * Example:
 * ```kotlin
 * JoinFetch("address")                          // Fetch user's address
 * JoinFetch("orders", JoinType.LEFT)            // Left join fetch orders
 * JoinFetch("user.address", true)               // Nested fetch through user
 * JoinFetch("pets", "userPets", false)          // With custom alias
 * ```
 */
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
