package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Notice
import org.slf4j.LoggerFactory
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

class NoticesTableModel: AbstractTableModel() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)

    private var notices: MutableList<Notice> = mutableListOf<Notice>()

    fun setNoticesList(notices: MutableList<Notice>) {
        this.notices = notices
        fireTableDataChanged()
    }

    override fun getRowCount(): Int {
        return notices.size
    }

    override fun getColumnCount(): Int {
        return COLUMN_NAMES.size
    }

    fun getNoticeAt(rowIndex: Int): Notice {
        log.debug("getNoticeAt(rowIndex=$rowIndex)")
        return notices[rowIndex]
    }

    /**
     * fügt eine Meldung am Anfang der Tabelle hinzu,
     * und aktualisiert die View(s)
     */
    fun addNotice(notice: Notice) {
        log.debug("add notice #${notice.id}")
        notices.add(0, notice)
        fireTableRowsInserted(0,0)
    }

    /**
     * aktualisiert die View(s) nach Änderungen
     */
    fun updateNotice(notice: Notice) {
        log.debug("update notice #${notice.id}")
        val rowIndex = notices.indexOf(notice)
        fireTableRowsUpdated(rowIndex, rowIndex)
    }

    /**
     * entfernt eine Meldung
     * und aktualisiert die View(s)
     */
    fun removeNotice(notice: Notice) {
        log.debug("remove notice #${notice.id}")
        val rowIndex = notices.indexOf(notice)
        notices.remove(notice)
        fireTableRowsDeleted(rowIndex, rowIndex)
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        log.debug("getValueAt(rowIndex=$rowIndex, columnIndex=$columnIndex)")
        val notice = notices[rowIndex]
        return when (columnIndex) {
            0 -> notice.id
            1 -> notice.countrySymbol
            2 -> notice.licensePlate
            3 -> notice.vehicleMake
            4 -> VehicleColor.fromColorName(notice.color)
            5 -> notice.getCreatedTimeFormatted()
            6 -> notice.getObservationTimeFormatted()
            7 -> notice.getState()
            else -> IndexOutOfBoundsException()
        }
    }

    override fun getColumnName(column: Int): String {
        //return super.getColumnName(column)
        return COLUMN_NAMES[column]
    }

    companion object {
        val COLUMN_NAMES = arrayOf(
            "#", "Land", "Kennzeichen", "Marke", "Farbe", "Erstellt", "Beobachtet", "Status"
        )
    }

}