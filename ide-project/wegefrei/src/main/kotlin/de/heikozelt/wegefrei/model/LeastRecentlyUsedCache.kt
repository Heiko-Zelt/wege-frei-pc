package de.heikozelt.wegefrei.model

import org.slf4j.LoggerFactory

/**
 * Speichert eine limitierte Zahl von Elementen.
 * Zugriff, also Hinzufügen und Lesen von Elementen über eine ID.
 * Wenn das Limit erreicht ist,
 * werden alte Elemente nach der Least Recently Used-Strategie rausgeschmissen.
 *
 * Komplikation: Wenn ein Element mehrmals hinzugefügt wird,
 * dann ist es neu und muss es an das Ende der Queue verschoben werden.
 * (oder entfernt und ein neues Element angehängt werden.)
 * todo Prio 4 Optimierung: Verschieben statt entfernen und hinzufügen
 */
class LeastRecentlyUsedCache<K, V>(private var limit: Int) {
    class QueueEntry<K, V>(val id: K, var element: V, var previous: QueueEntry<K, V>?, var next: QueueEntry<K, V>?)
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    /**
     * Ein "Primär-Index" = Abbildung von ID zu Element, um Elemente schnell wiederzufinden.
     */
    private val map = HashMap<K, QueueEntry<K, V>>()

    /**
     * The Queue is just a double linked list
     */
    private var first: QueueEntry<K, V>? = null
    private var last: QueueEntry<K, V>? = null

    private fun removeEntryFromQueue(existingEntry: QueueEntry<K, V>) {
        log.debug("  removeEntryFromQueue(existingEntry: id=${existingEntry.id}, element=${existingEntry.element})")
        existingEntry.previous?.let {
            it.next = existingEntry.next
        }
        existingEntry.next?.let {
            it.previous = existingEntry.previous
        }
        if(first == existingEntry) {
            first = existingEntry.next
        }
        if(last == existingEntry) {
            last = existingEntry.previous
        }
    }

    private fun removeFirstEntryFromQueue() {
        log.debug("  removeFirstEntryFromQueue(first: id=${first?.id}, element=${first?.element})")
        first?.next?.previous = null
        first = first?.next
    }

    private fun appendEntryToQueue(id: K, element: V): QueueEntry<K, V> {
        log.debug("  appendEntryToQueue(id=$id, element=$element)")
        val newEntry = QueueEntry(id, element, last, null)
        last?.next = newEntry
        last = newEntry
        if(first == null) {
            first = newEntry
        }
        return newEntry
    }

    /**
     * could be optimized, if existing entry is not removed and recreated but moved
     */
    @Synchronized
    operator fun set(id: K, element: V) {
        log.debug("add(id=$id, element=$element)")
        // remove an existing entry, if an entry with this id already exists
        val existingEntry = map[id]
        existingEntry?.let {
            removeEntryFromQueue(existingEntry)
            map.remove(id)
        }

        val newEntry = appendEntryToQueue(id, element)
        map[id] = newEntry

        // wenn die Queue voll ist, den ersten Eintrag wieder herauswerfen
        if (map.size > limit) {
            map.remove(first?.id)
            removeFirstEntryFromQueue()
        }
    }

    @Synchronized
    operator fun get(id: K): V? {
        log.debug("get(id=$id)")
        return map[id]?.element
    }
}