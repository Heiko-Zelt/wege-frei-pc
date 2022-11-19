package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.DatabaseRepo
import de.heikozelt.wegefrei.entities.NoticeEntity
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import javax.swing.table.AbstractTableModel

/**
 * Im Gegensatz zum DefaultTableModel werden die Daten nicht in einem 2-dimensionalen Vector
 * (Vector dessen Elemente wiederum Vector-Objekte sind) gespeichert,
 * sondern in einer MutableList mit Notice-Objekten.
 *
 * Einige Methoden sind ähnlich, wie im DefaultTableModel implementiert.
 * Es werden aber nicht alle Funktionen benötigt. Es sind also weniger Methoden implementiert.
 *
 * Aus dem AbstractTableModel wird z.B. die Funktionalität der Event-Benachrichtigung an die View JTable übernommen.
 */

class NoticesTableModel : AbstractTableModel() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    //private var noticeEntities: MutableList<NoticeEntity> = mutableListOf<NoticeEntity>()
    private val noticeIds = mutableListOf<Int>()

    private val cache = LeastRecentlyUsedCache<Int, Future<NoticeEntity?>>(300)
    private val executor = Executors.newFixedThreadPool(4)
    private var databaseRepo: DatabaseRepo? = null

    /**
     * sets the database repository and loads IDs of all notices
     */
    fun setDatabaseRepo(databaseRepo: DatabaseRepo) {
        this.databaseRepo = databaseRepo
        val ids = databaseRepo.findAllNoticesIdsDesc()
        noticeIds.clear()
        ids?.let {
            noticeIds.addAll(ids)
        }
        fireTableDataChanged()
    }

    override fun getRowCount(): Int {
        return noticeIds.size
    }

    override fun getColumnCount(): Int {
        return COLUMN_NAMES.size
    }

    fun getNoticeAt(rowIndex: Int): NoticeEntity {
        databaseRepo?.let { dbRepo ->
            log.debug("getNoticeAt(rowIndex=$rowIndex)")
            val entry = cache[rowIndex]
            return if (entry == null) { // Initial-Fall: Eintrag nicht im Cache und Laden noch nicht gestartet.
                val callable = Callable<NoticeEntity?> { dbRepo.findNoticeById(noticeIds[rowIndex]) }
                val futureTask = object: FutureTask<NoticeEntity?>(callable) {
                    override fun done() {
                        EventQueue.invokeLater {
                            fireTableRowsUpdated(rowIndex, rowIndex)
                        }
                    }
                }
                executor.execute(futureTask)
                cache[rowIndex] = futureTask
                val n = NoticeEntity()
                n.id = noticeIds[rowIndex]
                n
            } else {
                if (entry.isDone) {
                    var n = entry.get()
                    if (n == null) { // Fehler-Fall: Irgendetwas muss beim Laden schiefgelaufen sein.
                        n = NoticeEntity()
                        n.id = noticeIds[rowIndex]
                        n
                    } else { // Ideal-Fall, Eintrag im Cache und geladen.
                        n
                    }
                } else { // Geduld: Das Laden ist noch im Gange.
                    val n = NoticeEntity()
                    n.id = noticeIds[rowIndex]
                    n
                }
            }
        }
        val n = NoticeEntity()
        n.id = noticeIds[rowIndex]
        return n
    }

    /**
     * fügt eine Meldung am Anfang der Tabelle hinzu,
     * und aktualisiert die View(s)
     */
    fun addNotice(noticeEntity: NoticeEntity) {
        noticeEntity.id?.let { id ->
            log.debug("add notice #${noticeEntity.id}")
            //noticeEntities.add(0, noticeEntity)
            val callable = Callable { noticeEntity }
            val futureTask = FutureTask(callable)
            executor.execute(futureTask)
            cache[id] = futureTask
            noticeIds.add(0, id)
            fireTableRowsInserted(0, 0)
        }
    }

    /**
     * aktualisiert die View(s) nach Änderungen
     */
    fun updateNotice(noticeEntity: NoticeEntity) {
        log.debug("update notice #${noticeEntity.id}")
        val rowIndex = noticeIds.indexOf(noticeEntity.id)
        fireTableRowsUpdated(rowIndex, rowIndex)
    }

    /**
     * entfernt eine Meldung
     * und aktualisiert die View(s)
     */
    fun removeNotice(noticeEntity: NoticeEntity) {
        log.debug("remove notice #${noticeEntity.id}")
        val rowIndex = noticeIds.indexOf(noticeEntity.id)
        noticeIds.remove(noticeEntity.id)
        // löschen aus Cache erfolgt automatisch, wenn neue Einträge reinkommen
        // todo Prio 3: Speicher-Optimierung möglich, indem der Eintrag vorzeitig gelöscht wird
        fireTableRowsDeleted(rowIndex, rowIndex)
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        log.debug("getValueAt(rowIndex=$rowIndex, columnIndex=$columnIndex)")
        val notice = getNoticeAt(rowIndex)

        return when (columnIndex) {
            0 -> notice.id
            1 -> notice.countrySymbol
            2 -> notice.licensePlate
            3 -> notice.vehicleMake
            4 -> VehicleColor.fromColorName(notice.color)
            5 -> notice.getCreatedTimeFormatted()
            6 -> notice.getObservationTimeFormatted()
            7 -> notice.photoEntities.size
            8 -> notice.getState()
            else -> IndexOutOfBoundsException()
        }
    }

    override fun getColumnName(column: Int): String {
        //return super.getColumnName(column)
        return COLUMN_NAMES[column]
    }

    companion object {
        val COLUMN_NAMES = arrayOf(
            "#", "Land", "Kennzeichen", "Marke", "Farbe", "Erstellt", "Beobachtet", "Fotos", "Status"
        )
    }

}