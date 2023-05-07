package de.heikozelt.wegefrei.cache

import org.slf4j.LoggerFactory
import java.util.concurrent.LinkedBlockingDeque

/**
 * To be used in a GUI with a large list (JList/ListModel) or a large table (JTable/TableModel),
 * where the GUI should be responsive and the elements can't be loaded upfront at once.
 * The key may be a primary key or a row number.
 */
class CallbackCache<K, V>(sizeLimit: Int, findFunction: (K) -> (V?), callbackFunction: (V) -> Unit) {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * The cache, which stores the elements.
     */
    private val cache = LeastRecentlyUsedCache<K, V>(sizeLimit)

    /**
     * input queue for the loader thread. It stores the keys of the elements which are to be loaded.
     */
    private val queue = LinkedBlockingDeque<K>()

    /**
     * Loads the missing elements from the backend, stores them in the cache and calls back the consumer.
     */
    private var loader = LoaderThread(queue, findFunction, cache, callbackFunction)

    init {
        loader.start()
    }

    /**
     * If there is a cache hit, the element is returned immediately.
     * If there is a cache miss, null is returned,
     * a background thread loads the element from the backend and the consumer is called back later.
     */
    operator fun get(key: K): V? {
        log.debug("get(key=$key)")
        val element = cache[key]
        return if (element == null) {
            log.debug("cache miss")
            if (key !in queue) {
                log.debug("enqueue for loading")
                queue.add(key) // enqueue for loading
            }
            null
        } else {
            log.debug("returns $element")
            element // cache hit, success :-)
        }
    }

    /**
     * a shortcut to insert (or replace) an element into the cache without loading from the backend
     */
    operator fun set(key: K, value: V) {
        log.debug("set(key=$key, value=$value)")
        cache[key] = value // just delegate
    }

    fun removeKey(key: K) {
        log.debug("removeKey(key=$key)")
        cache.removeKey(key)
        queue.remove(key)
    }
}