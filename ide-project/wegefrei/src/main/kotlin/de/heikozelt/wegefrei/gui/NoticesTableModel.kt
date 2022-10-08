package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.databaseService
import de.heikozelt.wegefrei.entities.Notice
import javax.swing.JLabel
import javax.swing.table.AbstractTableModel;

class NoticesTableModel : AbstractTableModel() {

    private val notices = databaseService.getAllNotices()

    override fun getRowCount(): Int {
        return notices.size
    }

    override fun getColumnCount(): Int {
        return COLUMN_NAMES.size
    }

    fun getNoticeAt(rowIndex: Int): Notice {
        return notices[rowIndex]
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        val notice = notices[rowIndex]
        return when (columnIndex) {
            0 -> notice.id
            1 -> notice.getDateFormatted()
            2 -> notice.countrySymbol
            3 -> notice.licensePlate
            4 -> notice.vehicleMake
            5 -> notice.color
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