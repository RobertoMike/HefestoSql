package io.github.robertomike.hql.hefesto.constructors

import io.github.robertomike.hql.BaseTest
import io.github.robertomike.hefesto.enums.Operator
import io.github.robertomike.hefesto.enums.Sort
import io.github.robertomike.hefesto.hql.builders.Hefesto
import io.github.robertomike.hql.enums.Status
import io.github.robertomike.hql.hefesto.models.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Test class for Kotlin property references (KProperty1) in HQL API.
 * Tests the type-safe Kotlin property reference syntax using ::property notation.
 */
@ExtendWith(BaseTest::class)
class KotlinPropertyReferenceTest {

    @Test
    fun `where with Kotlin property reference`() {
        val result = Hefesto.make(User::class.java)
            .where(User::name, "test")
            .findFirst()

        assertTrue(result.isPresent)
        assertEquals("test@mail.com", result.get().email)
    }

    @Test
    fun `where with Kotlin property reference and operator`() {
        val result = Hefesto.make(User::class.java)
            .where(User::name, Operator.LIKE, "l%")
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.all { it.name?.startsWith("l") == true })
    }

    @Test
    fun `where with greater than operator`() {
        val result = Hefesto.make(User::class.java)
            .where(User::id, Operator.GREATER, 5L)
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.all { it.id > 5 })
    }

    @Test
    fun `whereIn with Kotlin property reference varargs`() {
        val result = Hefesto.make(User::class.java)
            .whereIn(User::name, "test", "petto")
            .get()

        assertFalse(result.isEmpty())
        assertEquals(2, result.size)
        assertTrue(result.all { it.name in listOf("test", "petto") })
    }

    @Test
    fun `whereIn with Kotlin property reference iterable`() {
        val names = listOf("test", "petto", "javi")
        val result = Hefesto.make(User::class.java)
            .whereIn(User::name, names)
            .get()

        assertFalse(result.isEmpty())
        assertEquals(3, result.size)
        assertTrue(result.all { it.name in names })
    }

    @Test
    fun `whereNotIn with Kotlin property reference`() {
        val result = Hefesto.make(User::class.java)
            .whereNotIn(User::name, "test", "petto")
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.none { it.name == "test" || it.name == "petto" })
    }

    @Test
    fun `whereNotIn with Kotlin property reference iterable`() {
        val excludedNames = listOf("test", "petto")
        val result = Hefesto.make(User::class.java)
            .whereNotIn(User::name, excludedNames)
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.none { it.name in excludedNames })
    }

    @Test
    fun `whereIsNull with Kotlin property reference`() {
        val result = Hefesto.make(User::class.java)
            .whereIsNull(User::photo)
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.all { it.photo == null })
    }

    @Test
    fun `whereIsNotNull with Kotlin property reference`() {
        val result = Hefesto.make(User::class.java)
            .whereIsNotNull(User::photo)
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.all { it.photo != null })
    }

    @Test
    fun `orWhere with Kotlin property reference`() {
        val result = Hefesto.make(User::class.java)
            .where(User::name, "test")
            .orWhere(User::name, "petto")
            .get()

        assertFalse(result.isEmpty())
        assertEquals(2, result.size)
        assertTrue(result.all { it.name in listOf("test", "petto") })
    }

    @Test
    fun `orWhere with Kotlin property reference and operator`() {
        val result = Hefesto.make(User::class.java)
            .where(User::id, Operator.LESS, 2L)
            .orWhere(User::id, Operator.GREATER, 7L)
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.all { it.id < 2 || it.id > 7 })
    }

    @Test
    fun `orderBy with Kotlin property reference ascending`() {
        val result = Hefesto.make(User::class.java)
            .orderBy(User::name)
            .limit(3)
            .get()

        assertEquals(3, result.size)
        // Verify ascending order
        for (i in 0 until result.size - 1) {
            assertTrue(result[i].name!! <= result[i + 1].name!!)
        }
    }

    @Test
    fun `orderBy with Kotlin property reference descending`() {
        val result = Hefesto.make(User::class.java)
            .orderBy(User::name, Sort.DESC)
            .limit(3)
            .get()

        assertEquals(3, result.size)
        // Verify descending order
        for (i in 0 until result.size - 1) {
            assertTrue(result[i].name!! >= result[i + 1].name!!)
        }
    }

    @Test
    fun `multiple orderBy with Kotlin property references`() {
        val result = Hefesto.make(User::class.java)
            .orderBy(User::status)
            .orderBy(User::name)
            .get()

        assertFalse(result.isEmpty())
        // Verify primary sort by status, secondary by name
        for (i in 0 until result.size - 1) {
            if (result[i].status == result[i + 1].status) {
                assertTrue(result[i].name!! <= result[i + 1].name!!)
            }
        }
    }

    @Test
    fun `addSelect with Kotlin property reference`() {
        val result = Hefesto.make(User::class.java)
            .addSelect(User::name)
            .addSelect(User::email)
            .limit(5)
            .findFor(User::class.java)

        assertFalse(result.isEmpty())
        assertEquals(5, result.size)
    }

    @Test
    fun `groupBy with Kotlin property reference`() {
        val result = Hefesto.make(User::class.java)
            .addSelect(User::status)
            .count("id")
            .groupBy(User::status)
            .get()

        assertFalse(result.isEmpty())
    }

    @Test
    fun `complex query with multiple Kotlin property references`() {
        val result = Hefesto.make(User::class.java)
            .where(User::name, Operator.LIKE, "%a%")
            .whereIsNotNull(User::email)
            .orderBy(User::name)
            .limit(10)
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.all { it.name?.contains("a") == true })
        assertTrue(result.all { it.email != null })
    }

    @Test
    fun `whereAny with Kotlin property references`() {
        val result = Hefesto.make(User::class.java)
            .whereAny { group ->
                group.where(User::name, "test")
                group.where(User::name, "petto")
                group.where(User::name, "javi")
            }
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.all { it.name in listOf("test", "petto", "javi") })
    }

    @Test
    fun `whereAll with Kotlin property references`() {
        val result = Hefesto.make(User::class.java)
            .whereAll { group ->
                group.whereIsNotNull(User::email)
                group.where(User::status, Operator.FIND_IN_SET, Status.ACTIVE.name)
            }
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.all { it.email != null && it.status.contains(Status.ACTIVE) })
    }

    @Test
    fun `nested conditional groups with Kotlin property references`() {
        val result = Hefesto.make(User::class.java)
            .whereAny { outer ->
                outer.where(User::name, "test")
                outer.whereAll { inner ->
                    inner.where(User::name, Operator.LIKE, "l%")
                    inner.whereIsNotNull(User::photo)
                }
            }
            .get()

        assertFalse(result.isEmpty())
    }

    @Test
    fun `mixed string and Kotlin property references`() {
        // This tests that both approaches can coexist
        val result = Hefesto.make(User::class.java)
            .where(User::name, Operator.LIKE, "%a%")  // Kotlin property
            .where("email", Operator.LIKE, "%@%")      // String reference
            .orderBy(User::id)                          // Kotlin property
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.all { it.name?.contains("a") == true })
        assertTrue(result.all { it.email?.contains("@") == true })
    }

    @Test
    fun `count with where using Kotlin property reference`() {
        val count = Hefesto.make(User::class.java)
            .where(User::name, Operator.LIKE, "l%")
            .countResults()

        assertTrue(count > 0)
    }

    @Test
    fun `findFirst with Kotlin property reference`() {
        val result = Hefesto.make(User::class.java)
            .where(User::name, "test")
            .orderBy(User::id)
            .findFirst()

        assertTrue(result.isPresent)
        assertEquals("test", result.get().name)
    }

    @Test
    fun `whereRaw with Kotlin property reference for comparison`() {
        // Test mixing whereRaw (HQL-specific) with Kotlin property references
        val result = Hefesto.make(User::class.java)
            .where(User::name, Operator.LIKE, "l%")
            .whereRaw("email IS NOT NULL")
            .get()

        assertFalse(result.isEmpty())
        assertTrue(result.all { it.name?.startsWith("l") == true })
        assertTrue(result.all { it.email != null })
    }

    @Test
    fun `aggregate functions with Kotlin property reference`() {
        val result = Hefesto.make(User::class.java)
            .where(User::status, Operator.FIND_IN_SET, Status.INACTIVE.name)
            .count(User::id)
            .findFirstFor(Long::class.java)

        assertNotNull(result)
        assertTrue(result > 0)
    }
}
