package de.heikozelt.wegefrei.model

import de.heikozelt.wegefrei.entities.Notice
import mu.KotlinLogging
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

class NoticesTableModel(private val notices: MutableList<Notice>) : AbstractTableModel() {
    private val log = KotlinLogging.logger {}

    override fun getRowCount(): Int {
        return notices.size
    }

    override fun getColumnCount(): Int {
        return COLUMN_NAMES.size
    }

    fun getNoticeAt(rowIndex: Int): Notice {
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
        val notice = notices[rowIndex]
        return when (columnIndex) {
            0 -> notice.id
            1 -> notice.getDateFormatted()
            2 -> notice.countrySymbol
            3 -> notice.licensePlate
            4 -> notice.vehicleMake
            5 -> ListColor.fromColorName(notice.color)
            else -> IndexOutOfBoundsException()
        }
    }

    override fun getColumnName(column: Int): String {
        //return super.getColumnName(column)
        return COLUMN_NAMES[column]
    }

    companion object {
        val COLUMN_NAMES = arrayOf(
            "#", "Datum/Zeit", "Land", "Kennzeichen", "Marke", "Farbe"
        )
    }

}