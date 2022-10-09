package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.App
import de.heikozelt.wegefrei.model.NoticesTableModel
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTable

class NoticesFrame(private val app: App) : JFrame("Meldungen - Wege frei!") {

    private val scrollPanel = JPanel(BorderLayout())
    private val noticesTableModel = NoticesTableModel(app.getDatabaseService().getAllNoticesDesc())
    private val noticesTable = JTable(noticesTableModel)
    private val mainToolBar = MainToolBar()

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
    fun noticeAdded() {
        // todo update Übersichtsseite
        // noticesTableModel ...
    }

    fun noticeUpdated() {
        // todo update Übersichtsseite
        // noticesTableModel ...
    }

}