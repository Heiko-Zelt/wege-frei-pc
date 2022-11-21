package de.heikozelt.wegefrei.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class LeastRecentlyUsedCacheTest {

    @Test
    fun shortStringCache_add_3_one_is_evicted() {
        val cache = LeastRecentlyUsedCache<Short, String>(2)
        assertNull(cache[1])
        assertNull(cache[2])

        cache[1] = "eins"
        assertEquals("eins", cache[1])
        assertNull(cache[2])
        assertNull(cache[3])

        cache[2] = "zwei"
        assertEquals("eins", cache[1])
        assertEquals("zwei", cache[2])
        assertNull(cache[3])

        cache[3] = "drei"
        assertNull(cache[1])
        assertEquals("zwei", cache[2])
        assertEquals("drei", cache[3])
    }

    @Test
    fun intCharCache_add_same_key_multiple_times() {
        val cache = LeastRecentlyUsedCache<Int, Char>(3)
        assertNull(cache[1])
        assertNull(cache[2])

        cache[11] = '1'
        assertEquals('1', cache[11])
        assertNull(cache[2])
        assertNull(cache[3])

        cache[11] = 'x'
        assertEquals('x', cache[11])
        assertNull(cache[2])
        assertNull(cache[3])

        cache[3] = '3'
        assertEquals('x', cache[11])
        assertNull(cache[2])
        assertEquals('3', cache[3])

        cache[3] = 'y'
        assertEquals('x', cache[11])
        assertNull(cache[2])
        assertEquals('y', cache[3])

        cache[3] = 'y'
        assertEquals('x', cache[11])
        assertNull(cache[2])
        assertEquals('y', cache[3])

        cache[0] = '0'
        assertEquals('0', cache[0])
        assertEquals('x', cache[11])
        assertNull(cache[2])
        assertEquals('y', cache[3])

        cache[7] = '7'
        assertEquals('0', cache[0])
        assertNull(cache[11])
        assertNull(cache[2])
        assertEquals('y', cache[3])
        assertEquals('7', cache[7])
    }

    @Test
    fun stringByteCache_moveToEnd() {
        val cache = LeastRecentlyUsedCache<String, Byte>(2)
        assertNull(cache["eins"])
        assertNull(cache["zwei"])

        cache["eins"] = 1
        assertEquals(1, cache["eins"])
        assertNull(cache["zwei"])

        cache["zwei"] = 2
        assertEquals(1, cache["eins"])
        assertEquals(2, cache["zwei"])

        cache["eins"] = 1
        assertEquals(1, cache["eins"])
        assertEquals(2, cache["zwei"])

        cache["drei"] = 3
        assertEquals(1, cache["eins"])
        assertNull(cache["zwei"])
        assertEquals(3, cache["drei"])
    }

    @Test
    fun transformKeys_Int() {
        fun transformation(key: Int): Int {
            return key + 1
        }

        val cache = LeastRecentlyUsedCache<Int, String>(2)
        cache[0] = "Z"
        cache[1] = "Y"
        cache.transformKeys { transformation(it) }
        assertNull(cache[0])
        assertEquals("Z", cache[1])
        assertEquals("Y", cache[2])
    }

    @Test
    fun transformKeys_with_predicate() {
        val cache = LeastRecentlyUsedCache<Int, String>(2)
        cache[0] = "Z"
        cache[1] = "Y"
        cache.transformKeys( transformation = { it + 1 }, predicate = { it == 1 })
        assertEquals("Z", cache[0])
        assertNull(cache[1])
        assertEquals("Y", cache[2])
        assertNull(cache[3])
    }

    @Test
    fun transformKeys_String() {
        val cache = LeastRecentlyUsedCache<String, Int>(2)
        cache["A"] = 99
        cache["B"] = 98
        //cache.transformKeys { transformation(it) }
        cache.transformKeys { it + "x" }
        assertNull(cache["A"])
        assertNull(cache["B"])
        assertNull(cache["x"])
        assertEquals(99, cache["Ax"])
        assertEquals(98, cache["Bx"])
    }

    @Test
    fun removeKey() {
        val cache = LeastRecentlyUsedCache<String, Int>(4)
        cache["A"] = 99
        cache["B"] = 98
        cache["C"] = 97
        cache.removeKey("B")
        assertEquals(99, cache["A"])
        assertNull(cache["B"])
        assertEquals(97, cache["C"])
        assertNull(cache["D"])
    }
}