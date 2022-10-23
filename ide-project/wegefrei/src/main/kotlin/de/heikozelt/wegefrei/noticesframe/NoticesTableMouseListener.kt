package de.heikozelt.wegefrei.noticesframe

import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.model.NoticesTableModel
import de.heikozelt.wegefrei.noticeframe.NoticeFrame
import java.awt.EventQueue
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTable

/**
 * Ein Doppelklick auf eine Meldung in der Tabelle öffnet ein Fenster zum Bearbeiten der Meldung.
 */
class NoticesTableMouseListener(private val app: WegeFrei): MouseAdapter() {
    override fun mouseClicked(me: MouseEvent) {
        if (me.clickCount == 2) { // double click
            val table = me.source as JTable
            val rowIndex = table.selectedRow
            //val column = table.selectedColumn
            //val id = table.getValueAt(rowIndex, 0) as Int
            //JOptionPane.showMessageDialog(null, "$row, $column, $id")
            val model = table.model as NoticesTableModel
            // open new Window to edit existing notice
            val notice = model.getNoticeAt(rowIndex)
            val editNoticeFrame = NoticeFrame(app)
            EventQueue.invokeLater {
                //Thread.sleep(5000) // simulate slowness
                editNoticeFrame.loadData(notice)
            }
        }
    }
}