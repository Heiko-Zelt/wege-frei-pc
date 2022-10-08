package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.entities.Notice
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTable

class NoticesFrame : JFrame("Meldungen - Wege frei!") {

    private val scrollPanel = JPanel(BorderLayout())
    private val noticesTable = JTable(NoticesTableModel())
    private val mainToolBar = MainToolBar()

    init {
        layout = BorderLayout()
        setSize(1000, 700)
        defaultCloseOperation = EXIT_ON_CLOSE

        add(mainToolBar, BorderLayout.NORTH)

        noticesTable.addMouseListener(NoticesTableMouseAdapter())
        //TableCellRenderer für Farbe als Icon
        scrollPanel.add(noticesTable.tableHeader, BorderLayout.NORTH)
        scrollPanel.add(noticesTable, BorderLayout.CENTER)
        add(scrollPanel)

        isVisible = true
    }

}