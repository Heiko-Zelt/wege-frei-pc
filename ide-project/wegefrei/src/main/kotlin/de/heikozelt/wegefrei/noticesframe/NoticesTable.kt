package de.heikozelt.wegefrei.noticesframe

import org.slf4j.LoggerFactory
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelEvent.INSERT

class NoticesTable(noticesTableModel: NoticesTableModel): JTable(noticesTableModel) {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    init {
        log.debug("init")
        log.debug("columnCount: ${getColumnCount()}")
        log.debug("noticesTableModel: $noticesTableModel")
        log.debug("model: $model")
        log.debug("model.columnCount: ${model.columnCount}")
        getSelectionModel().selectionMode = ListSelectionModel.SINGLE_SELECTION
        getColumn("Status").cellRenderer = NoticesTableStateCellRenderer()
        getColumn("Farbe").cellRenderer = NoticesTableColorCellRenderer()
    }

    /**
     * Auswahl auf erste Zeile setzen, nachdem ein Datensatz eingefügt wurde
     */
    override fun tableChanged(e: TableModelEvent) {
        super.tableChanged(e)
        if(e.type == INSERT) {
            changeSelection(0, 0, false, false)
        }
    }
}