package de.heikozelt.wegefrei.noticesframe

import de.heikozelt.wegefrei.db.DatabaseRepo
import de.heikozelt.wegefrei.db.NoticesObserver
import de.heikozelt.wegefrei.db.entities.NoticeEntity
import de.heikozelt.wegefrei.model.LeastRecentlyUsedCache
import de.heikozelt.wegefrei.model.RowNumIdMapDesc
import de.heikozelt.wegefrei.model.VehicleColor
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import java.time.ZonedDateTime
import java.util.concurrent.*
import javax.swing.table.AbstractTableModel

/**
 * Im Gegensatz zum DefaultTableModel werden die Daten nicht in einem 2-dimensionalen Vector
 * (Vector dessen Elemente wiederum Vector-Objekte sind) gespeichert,
 * sondern in einem Cache.
 * Cache-Einträge werden bei Bedarf asynchron via LoaderThread aus der Datenbank geladen.
 *
 * Einige Methoden sind ähnlich, wie im DefaultTableModel implementiert.
 * Es werden aber nicht alle Funktionen benötigt. Es sind also weniger Methoden implementiert.
 *
 * Aus dem AbstractTableModel wird z.B. die Funktionalität der Event-Benachrichtigung an die View JTable übernommen.
 * todo Prio 3: angezeigte Spalte konfigurierbar
 * todo Prio 3: Sortierung konfigurierbar
 */

class NoticesTableModel : AbstractTableModel(), NoticesObserver {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    // Abbildung von Notice-IDs auf Zeilennummern und umgekehrt
    private val noticeIds = RowNumIdMapDesc<Int>() // = arrayListOf()

    /**
     * Schlüssel sind Notice-IDs.
     * Ein Cache wird benötig, sonst müsste für jede Zelle (nicht nur jeden Datensatz) die Datenbank abgefragt werden.
     */
    private val cache = LeastRecentlyUsedCache<Int, NoticeEntity>(300)

    private val queue = LinkedBlockingDeque<Int>()
    private var loader: LoaderThread<Int, NoticeEntity>? = null
    private var databaseRepo: DatabaseRepo? = null

    init {
        log.debug("init")
        //log.debug("columnCount: ${getColumnCount()}")
    }

    /**
     * called by Background/LoaderThread
     */
    private fun noticeLoaded(noticeEntity: NoticeEntity) {
        noticeEntity.id?.let {
            // Wechsel zu Main/GUI-Thread, da Swing sonst nicht thread save ist
            EventQueue.invokeLater {
                val rowIndex = noticeIds.rowNumById(it)
                fireTableRowsUpdated(rowIndex, rowIndex)
            }
        }
    }

    /**
     * sets the database repository and loads IDs of all notices
     */
    fun setDatabaseRepo(databaseRepo: DatabaseRepo) {
        this.databaseRepo = databaseRepo
        val ids = databaseRepo.findAllNoticesIdsDesc()
        noticeIds.replaceAll(ids)
        loader?.interrupt()
        loader = LoaderThread(queue, databaseRepo::findNoticeById, cache, ::noticeLoaded)
        loader?.start()
        databaseRepo.subscribe(this)
        fireTableDataChanged()
    }

    override fun getRowCount(): Int {
        return noticeIds.getSize()
    }

    override fun getColumnCount(): Int {
        return COLUMN_NAMES.size
    }

    /**
     * todo: im Cache nach Notice.ID speichern statt nach rowIndex oder alle Cache-Einträge anpassen
     * ähnliches Problem beim Löschen. Oder einfach Cache leeren?
     */
    fun getNoticeAt(rowIndex: Int): NoticeEntity {
        log.debug("getNoticeAt(rowIndex=$rowIndex)")
        val noticeId = noticeIds.idByRowNum(rowIndex)
        val noticeEntity = cache[noticeId]
        return if (noticeEntity == null) {
            if (noticeId !in queue) {
                queue.add(noticeId) // enqueue for loading
            }
            NoticeEntity() // empty notice
        } else {
            noticeEntity // cache hit, success :-)
        }
    }

    @Deprecated("replaced by noticeInserted")
    fun addNotice(noticeEntity: NoticeEntity) {
        log.warn("addNotice(${noticeEntity.id})")
    }

    /**
     * fügt eine Meldung am Anfang der Tabelle hinzu,
     * und aktualisiert die View(s).
     * Man könnte statt add/update/delete auch jedes Mal die ganze Tabelle neu aufbauen.
     */
    override fun noticeInserted(noticeEntity: NoticeEntity) {
        log.debug("noticeInserted(id=${noticeEntity.id})")
        noticeEntity.id?.let { id ->
            EventQueue.invokeLater {
                cache[id] = noticeEntity
                noticeIds.push(id)
                log.debug("fireTableRowsInserted(0, 0)")
                fireTableRowsInserted(0, 0)
            }
        }
    }

    /**
     * aktualisiert die View(s) nach Änderungen
     * todo: eigentlich wird nur die id benötigt, nicht das ganze Objekt
     */
    fun updateNotice(noticeEntity: NoticeEntity) {
        log.warn("update notice #${noticeEntity.id}")
    }

    // todo Prio 1: replace by noticeUpdated()
    fun updateNoticeSent(noticeID: Int, sentTime: ZonedDateTime) {
        log.warn("updateNotice(${noticeID})")
        /*
        val rowIndex = noticeIds.rowNumById(noticeID)
        cache[rowIndex]?.get()?.sentTime = sentTime
        fireTableRowsUpdated(rowIndex, rowIndex)
        */
    }

    override fun noticeUpdated(noticeEntity: NoticeEntity) {
        log.debug("update notice #${noticeEntity.id}")
        noticeEntity.id?.let { id ->
            EventQueue.invokeLater {
                val rowIndex = noticeIds.rowNumById(id)
                cache[id] = noticeEntity
                fireTableRowsUpdated(rowIndex, rowIndex)
            }
        }
    }

    /**
     * entfernt eine Meldung
     * und aktualisiert die View(s)
     */
    @Deprecated("replaced by noticeDeleted")
    fun removeNotice(noticeEntity: NoticeEntity) {
        log.warn("remove notice #${noticeEntity.id}")
    }

    override fun noticeDeleted(noticeEntity: NoticeEntity) {
        noticeEntity.id?.let { id ->
            EventQueue.invokeLater {
                val rowIndex = noticeIds.rowNumById(id)
                noticeIds.delete(rowIndex)
                cache.removeKey(id)
                fireTableRowsDeleted(rowIndex, rowIndex)
            }
        }
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        log.debug("getValueAt(rowIndex=$rowIndex, columnIndex=$columnIndex)")
        val notice = getNoticeAt(rowIndex)

        return when (columnIndex) {
            0 -> {
                log.debug("notice.id=${notice.id}"); notice.id
            }

            1 -> notice.countrySymbol
            2 -> notice.licensePlate
            3 -> notice.vehicleMake
            4 -> VehicleColor.fromColorName(notice.color)
            6 -> notice.getObservationTimeFormatted()
            5 -> notice.getCreatedTimeFormatted()
            7 -> notice.photoEntities.size
            8 -> notice.getState()
            9 -> notice.getSentTimeFormatted()
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun getColumnName(column: Int): String {
        //return super.getColumnName(column)
        return COLUMN_NAMES[column]
    }

    companion object {
        val COLUMN_NAMES = arrayOf(
            "#", "Land", "Kennzeichen", "Marke", "Farbe", "Tatdatum, Uhrzeit", "Erstellt", "Fotos", "Status", "gesendet"
        )
    }
}