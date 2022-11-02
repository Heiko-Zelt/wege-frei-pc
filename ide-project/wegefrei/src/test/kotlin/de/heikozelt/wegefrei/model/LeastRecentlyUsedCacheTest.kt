package de.heikozelt.wegefrei.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class LeastRecentlyUsedCacheTest {

    @Test
    fun shortStringCache_add_3_one_is_evicted() {
        val cache = LeastRecentlyUsedCache<Short, String>(2)
        assertNull(cache.get(1))
        assertNull(cache.get(2))

        cache.add(1, "eins")
        assertEquals("eins", cache.get(1))
        assertNull(cache.get(2))
        assertNull(cache.get(3))

        cache.add(2, "zwei")
        assertEquals("eins", cache.get(1))
        assertEquals("zwei", cache.get(2))
        assertNull(cache.get(3))

        cache.add(3, "drei")
        assertNull(cache.get(1))
        assertEquals("zwei", cache.get(2))
        assertEquals("drei", cache.get(3))
    }

    @Test
    fun intCharCache_add_same_key_multiple_times() {
        val cache = LeastRecentlyUsedCache<Int, Char>(3)
        assertNull(cache.get(1))
        assertNull(cache.get(2))

        cache.add(11, '1')
        assertEquals('1', cache.get(11))
        assertNull(cache.get(2))
        assertNull(cache.get(3))

        cache.add(11, 'x')
        assertEquals('x', cache.get(11))
        assertNull(cache.get(2))
        assertNull(cache.get(3))

        cache.add(3, '3')
        assertEquals('x', cache.get(11))
        assertNull(cache.get(2))
        assertEquals('3', cache.get(3))

        cache.add(3, 'y')
        assertEquals('x', cache.get(11))
        assertNull(cache.get(2))
        assertEquals('y', cache.get(3))

        cache.add(3, 'y')
        assertEquals('x', cache.get(11))
        assertNull(cache.get(2))
        assertEquals('y', cache.get(3))

        cache.add(0, '0')
        assertEquals('0', cache.get(0))
        assertEquals('x', cache.get(11))
        assertNull(cache.get(2))
        assertEquals('y', cache.get(3))

        cache.add(7, '7')
        assertEquals('0', cache.get(0))
        assertNull(cache.get(11))
        assertNull(cache.get(2))
        assertEquals('y', cache.get(3))
        assertEquals('7', cache.get(7))
    }
}