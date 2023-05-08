package de.heikozelt.wegefrei.cache

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

class CallbackCacheTest {

    /**
     * very simple mock of a TableModel or ListModel.
     * just logs the calls to the consume method.
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
        sleep(500)
        assertEquals(0, consumer.list.size)
    }

    @Test
    fun emptyCache_cacheMiss_callback() {
        val consumer = Consumer()
        val backend = mapOf(1 to "A", 2 to "B", 3 to "C")
        val cache = CallbackCache(10, backend::get, consumer::consume)
        assertNull(cache[1])
        sleep(500)
        assertEquals(1, consumer.list.size)
        assertEquals("A", consumer.list[0])
    }

    @Test
    fun emptyCache_cacheMiss_backendMiss() {
        val consumer = Consumer()
        val backend = mapOf(26 to "Z")
        val cache = CallbackCache(10, backend::get, consumer::consume)
        assertNull(cache[1])
        sleep(500)
        assertEquals(0, consumer.list.size)
    }

    @Test
    fun someMisses_then_someHits() {
        fun backend(i: Int): String {
            return "${('A'.code + i).toChar()}"
        }
        val consumer = Consumer()
        val cache = CallbackCache(26, ::backend, consumer::consume)

        // 26 cache misses
        for(i in 0 until 26) {
            assertNull(cache[i])
        }
        sleep(1 * 1000)
        for(i in 0 until 26) {
            assertEquals(backend(i), consumer.list[i])
        }
        // 26 cache hits
        for(i in 0 until 26) {
            assertEquals(backend(i), cache[i])
        }
    }

    @Test
    fun manyMisses_then_manyHits() {
        var x = 0L
        fun consumer(s: String) {
            x = s.toLong()
        }

        val cache = CallbackCache(1000, Long::toString, ::consumer)

        // many cache misses
        for(i in 0L until 2000L) {
            assertNull(cache[i])
        }
        sleep(5 * 1000)
        // 1000 cache hits
        for(i in 1000L  until 2000L) {
            assertEquals(i.toString(), cache[i])
        }
        // 1000 cache misses
        for(i in 0L  until 1000L) {
            assertNull(cache[i])
        }
    }
}