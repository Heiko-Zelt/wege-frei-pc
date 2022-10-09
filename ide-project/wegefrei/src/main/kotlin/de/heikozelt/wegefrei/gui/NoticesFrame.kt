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
    private val mainToolBar = MainToolBar(app)

    init {
        layout = BorderLayout()
        setSize(1000, 700)
        defaultCloseOperation = EXIT_ON_CLOSE

        add(mainToolBar, BorderLayout.NORTH)

        noticesTable.addMouseListener(NoticesTableMouseAdapter(app))
        //TableCellRenderer für Farbe als Icon
        scrollPanel.add(noticesTable.tableHeader, BorderLayout.NORTH)
        scrollPanel.add(noticesTable, BorderLayout.CENTER)
        add(scrollPanel)

        isVisible = true
    }

    /**
     * called, when new notice is saved, added to database
     */
    fun noticeAdded(notice: Notice) {
        // todo update Übersichtsseite
        noticesTableModel.addNotice(notice)
    }

    fun noticeUpdated(notice: Notice) {
        // todo update Übersichtsseite
        // noticesTableModel ...
    }

    fun noticeDeleted(notice: Notice) {
        // todo implement
    }

}