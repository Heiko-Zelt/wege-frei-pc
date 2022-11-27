package de.heikozelt.wegefrei.noticesframe

import de.heikozelt.wegefrei.WegeFrei
import org.slf4j.LoggerFactory
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTable

/**
 * Ein Doppelklick auf eine Meldung in der Tabelle öffnet ein Fenster zum Bearbeiten der Meldung.
 */
class NoticesTableMouseListener(private val app: WegeFrei): MouseAdapter() {
    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    override fun mouseClicked(me: MouseEvent) {
        if (me.clickCount == 2) { // double click
            val table = me.source
            if(table is JTable) {
                val rowIndex = table.selectedRow
                val model = table.model
                if(model is NoticesTableModel) {
                    val notice = model.getNoticeAt(rowIndex)
                    app.openNoticeFrame(notice)
                } else {
                    log.error("Model ist keine NoticesTableModel")
                }
            } else {
                log.error("Quelle ist keine JTable")
            }
        }
    }
}