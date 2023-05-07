package de.heikozelt.wegefrei.cache

import java.util.concurrent.LinkedBlockingDeque

/**
 * Läd Meldungen für das NoticesTableModel
 * @param queue Eingangswarteschlange mit zu lesenden IDs.
 * @param dbRepo Datenbank mit Notices
 * @param cache LRU Cache mit Abbildung id zu NoticeEntity
 * @param tableModel NoticesTableModel
 */
class LoaderThread<T, U>(
    private val queue: LinkedBlockingDeque<T>,
    private val findFunction: (T) -> (U?),
    private val cache: LeastRecentlyUsedCache<T, U>,
    private val consumeFunction: (U) -> Unit
): Thread() {

    /**
     * in einer Endlosschleife:
     * <ol>
     *   <li>liest eine Meldungs-ID aus der Eingangswarteschlange (wartet wenn leer)</li>
     *   <li>Sucht die entsprechende Meldung in der Datenbank</li>
     *   <li>Schreibt die Meldung in den Zwischenspeicher</li>
     * </ol>
     */
    override fun run() {
        while(true) {
            val id = queue.take()
            val entity = findFunction(id)
            // zwischenzeitlich gelöscht?
            entity?.let {
                cache[id] = it
                consumeFunction(it)
            }
        }
    }
}