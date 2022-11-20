package de.heikozelt.wegefrei.model

import org.slf4j.LoggerFactory

/**
 * Speichert eine limitierte Zahl von Elementen.
 * Zugriff, also Hinzufügen und Lesen von Elementen über einen Schlüssel/eine ID.
 * Wenn das Limit erreicht ist,
 * werden alte Elemente nach der Least Recently Used-Strategie rausgeschmissen.
 *
 * Komplikation: Wenn ein Element mehrmals hinzugefügt wird,
 * dann ist es neu und muss es an das Ende der Queue verschoben werden.
 * (oder entfernt und ein neues Element angehängt werden.)
 * todo Prio 4 Optimierung: Verschieben statt entfernen und hinzufügen
 */
class LeastRecentlyUsedCache<K, V>(private var limit: Int) {
    /**
     * Node represents an entry in the HashMap as well as an entry in the queue/doubly linked list
     */
    class Node<K, V>(val key: K, var element: V, var previous: Node<K, V>?, var next: Node<K, V>?)

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * Ein "Primär-Index" = Abbildung von Schlüssel zu Element, um Elemente schnell wiederzufinden
     * und nicht die gesamte Liste scannen zu müssen.
     */
    private val map = HashMap<K, Node<K, V>>()

    /**
     * A Queue remembers the order of the entries.
     * The Queue is a doubly linked list.
     * first is the queue head, dequeue
     */
    private var first: Node<K, V>? = null

    /**
     * last is the queue tail, enqueue
     */
    private var last: Node<K, V>? = null

    private fun removeNodeFromQueue(existingNode: Node<K, V>) {
        log.debug("  removeNodeFromQueue(existingEntry: key=${existingNode.key}, element=${existingNode.element})")
        existingNode.previous?.let {
            it.next = existingNode.next
        }
        existingNode.next?.let {
            it.previous = existingNode.previous
        }
        if(first == existingNode) {
            first = existingNode.next
        }
        if(last == existingNode) {
            last = existingNode.previous
        }
    }

    /**
     * remove first entry from the queue
     */
    private fun evict() {
        log.debug("  evict(first: key=${first?.key}, element=${first?.element})")
        first?.next?.previous = null
        first = first?.next
    }

    /**
     * adds an entry to the end of the queue
     */
    private fun enqueue(key: K, element: V): Node<K, V> {
        log.debug("  enqueue(key=$key, element=$element)")
        val newNode = Node(key, element, last, null)
        last?.next = newNode
        last = newNode
        if(first == null) {
            first = newNode
        }
        return newNode
    }

    /**
     * moves the node from somewhere in the queue to the end of the queue
     */
    private fun moveNodeToEnd(node: Node<K, V>) {
        log.debug("  moveNodeToEnd(entry: key=${node.key}, element=${node.element})")
        if(last == node) {
            return
        }
        node.previous?.let {
            it.next = node.next
        }
        node.next?.let {
            it.previous = node.previous
        }
        if(first == node) {
            first = node.next
        }
        last?.next = node
        last = node
        if(first == null) {
            first = node
        }
    }

    /**
     * insert or update an element
     */
    @Synchronized
    operator fun set(key: K, element: V) {
        log.debug("set(key=$key, element=$element)")
        val existingNode = map[key]
        existingNode?.let {
            moveNodeToEnd(existingNode)
            existingNode.element = element
            return
        }

        val newNode = enqueue(key, element)
        map[key] = newNode

        // wenn die Queue voll ist, den ersten Eintrag wieder herauswerfen
        if (map.size > limit) {
            map.remove(first?.key)
            evict()
        }
    }

    /**
     * returns element, if it exists, null otherwise
     */
    @Synchronized
    operator fun get(key: K): V? {
        log.debug("get(key=$key)")
        return map[key]?.element
    }
}