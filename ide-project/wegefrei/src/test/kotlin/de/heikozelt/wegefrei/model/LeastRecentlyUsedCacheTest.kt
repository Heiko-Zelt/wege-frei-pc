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
}