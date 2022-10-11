package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.App
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.model.NoticesTableModel
import java.awt.BorderLayout
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTable

class NoticesFrame(private val app: App) : JFrame("Meldungen - Wege frei!") {

    private val scrollPanel = JPanel(BorderLayout())
    private val noticesTableModel = NoticesTableModel(LinkedList(app.getDatabaseService().getAllNoticesDesc()))
    private val noticesTable = JTable(noticesTableModel)
    private val noticesToolBar = NoticesButtonsBar(app)

    init {
        layout = BorderLayout(5,5)
        setSize(1000, 700)
        defaultCloseOperation = EXIT_ON_CLOSE
        background = Styles.FRAME_BACKGROUND

        noticesTable.addMouseListener(NoticesTableMouseAdapter(app))
        //noticesTable.setDefaultRenderer(ListColor::class.java, NoticesTableCellRenderer())
        noticesTable.getColumn("Farbe").cellRenderer = NoticesTableColorCellRenderer()
        noticesTable.getColumn("Status").cellRenderer = NoticesTableStateCellRenderer()
        noticesTable.background = Styles.NOTICES_TABLE_BACKGROUND
        noticesTable.foreground = Styles.TEXT_COLOR
        scrollPanel.add(noticesTable.tableHeader, BorderLayout.NORTH)
        scrollPanel.add(noticesTable, BorderLayout.CENTER)

        add(scrollPanel)
        add(noticesToolBar, BorderLayout.SOUTH)

        isVisible = true
    }

    /**
     * called, when new notice is saved, added to database
     */
    fun noticeAdded(notice: Notice) {
        noticesTableModel.addNotice(notice)
    }

    fun noticeUpdated(notice: Notice) {
        noticesTableModel.updateNotice(notice)
    }

    fun noticeDeleted(notice: Notice) {
        noticesTableModel.removeNotice(notice)
    }

}