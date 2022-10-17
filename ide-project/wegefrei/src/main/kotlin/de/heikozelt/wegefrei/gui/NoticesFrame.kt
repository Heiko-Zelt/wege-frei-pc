package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.App
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.jobs.LoadNoticesWorker
import de.heikozelt.wegefrei.model.NoticesTableModel
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTable

/**
 * Die Initialisierung erfolgt in 2 Schritten:
 * <ol>
 *   <li>Die GUI wird aufgebaut.
 *       (Konstruktor: a. private Felder werden initialisiert, b. init()-Methode wird ausgeführt).
 *       Der Anwender sieht schon mal das Fenster mit den GUI-Widgets, hat also ein visuelles Feedback.</li>
 *   <li>loadData()-Methode läd die GUI (nur die Tabelle) mit Daten.
 *       Das kann etwas länger dauern und läuft in einem Hintergrund-Thread.</li>
 * </ol>
 */
class NoticesFrame(private val app: App) : JFrame("Meldungen - Wege frei!") {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val scrollPanel = JPanel(BorderLayout())
    private val noticesTableModel = NoticesTableModel()
    private val noticesTable = JTable(noticesTableModel)
    private val noticesToolBar = NoticesButtonsBar(app)

    init {
        log.debug("init")
        layout = BorderLayout(5,5)
        setSize(1000, 700)
        defaultCloseOperation = EXIT_ON_CLOSE
        background = Styles.FRAME_BACKGROUND

        noticesTable.addMouseListener(NoticesTableMouseListener(app))
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

    fun loadData() {
        val worker = LoadNoticesWorker(app.getDatabaseService(), noticesTableModel)
        worker.execute()
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