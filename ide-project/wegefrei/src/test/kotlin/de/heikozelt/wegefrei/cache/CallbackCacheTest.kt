package de.heikozelt.wegefrei.cache

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CallbackCacheTest {

    /**
     * very simple mock of a TableModel or ListModel
     */
    class Consumer {
        val list = mutableListOf<String>()
        fun consume(element: String) {
            this.list.add(element)
        }
    }

    @Test
    fun cacheHit_returnElementImmediately() {
        val consumer = Consumer()
        val backend = mapOf(1 to "A", 2 to "B", 3 to "C")
        val cache = CallbackCache(10, backend::get, consumer::consume)
        cache[1] = "A"
        assertEquals("A", cache[1])
        Thread.sleep(1000)
        assertEquals(0, consumer.list.size)
    }

    @Test
    fun emptyCache_cacheMiss_callback() {
        val consumer = Consumer()
        val backend = mapOf(1 to "A", 2 to "B", 3 to "C")
        val cache = CallbackCache(10, backend::get, consumer::consume)
        assertNull(cache[1])
        Thread.sleep(1000)
        assertEquals(1, consumer.list.size)
        assertEquals("A", consumer.list[0])
    }

    @Test
    fun emptyCache_cacheMiss_backendMiss() {
        val consumer = Consumer()
        val backend = mapOf(26 to "Z")
        val cache = CallbackCache(10, backend::get, consumer::consume)
        assertNull(cache[1])
        Thread.sleep(1000)
        assertEquals(0, consumer.list.size)
    }

}