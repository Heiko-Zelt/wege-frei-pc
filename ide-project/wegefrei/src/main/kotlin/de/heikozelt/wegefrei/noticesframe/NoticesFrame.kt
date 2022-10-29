package de.heikozelt.wegefrei.noticesframe

import de.heikozelt.wegefrei.WegeFrei
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.jobs.LoadNoticesWorker
import de.heikozelt.wegefrei.model.NoticesTableModel
import org.slf4j.LoggerFactory
import javax.swing.*

/**
 * Der NoticesFrame zeigt eine Übersichts-Tabelle mit allen Meldungen an.
 *
 * Die Initialisierung erfolgt in 2 Schritten:
 * <ol>
 *   <li>Die GUI wird aufgebaut.
 *       (Konstruktor: a. private Felder werden initialisiert, b. init()-Methode wird ausgeführt).
 *       Der Anwender sieht schon mal das Fenster mit den GUI-Widgets, hat also ein visuelles Feedback.</li>
 *   <li>loadData()-Methode läd die GUI (nur die Tabelle) mit Daten.
 *       Das kann etwas länger dauern und läuft in einem Hintergrund-Thread.</li>
 * </ol>
 */
class NoticesFrame(private val app: WegeFrei) : JFrame("Meldungen - Wege frei!") {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private val noticesTableModel = NoticesTableModel()


    init {
        log.debug("init")

        // GUI components
        val noticesTable = JTable(noticesTableModel)
        noticesTable.addMouseListener(NoticesTableMouseListener(app))
        noticesTable.getColumn("Farbe").cellRenderer = NoticesTableColorCellRenderer()
        noticesTable.getColumn("Status").cellRenderer = NoticesTableStateCellRenderer()
        val scrollPane = JScrollPane(noticesTable)
        val newButton = JButton("neue Meldung erfassen")
        newButton.addActionListener { app.openNoticeFrame() }
        val scanButton = JButton("Scan")
        scanButton.addActionListener { app.scanForNewPhotos() }
        val settingsButton = JButton("Einstellungen")
        settingsButton.addActionListener{ app.openSettingsFrame() }
        val helpButton = JButton("Hilfe")
        helpButton.addActionListener { log.debug("unhandled event") }

        // layout:
        val lay = GroupLayout(contentPane)
        lay.autoCreateGaps = true
        lay.autoCreateContainerGaps = true
        // left to right
        lay.setHorizontalGroup(
            lay.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane)
                .addGroup(
                    lay.createSequentialGroup()
                        .addPreferredGap(
                            LayoutStyle.ComponentPlacement.RELATED,
                            GroupLayout.PREFERRED_SIZE,
                            Int.MAX_VALUE
                        )
                        .addComponent(newButton)
                        .addComponent(scanButton)
                        .addComponent(settingsButton)
                        .addComponent(helpButton)
                )
        )
        // top to bottom
        lay.setVerticalGroup(
            lay.createSequentialGroup()
                .addComponent(scrollPane)
                .addGroup(
                    lay.createParallelGroup()
                        .addComponent(newButton)
                        .addComponent(scanButton)
                        .addComponent(settingsButton)
                        .addComponent(helpButton)
                )
        )
        layout = lay

        /*
        layout = BorderLayout(5,5)
        background = Styles.FRAME_BACKGROUND
        noticesTable.background = Styles.NOTICES_TABLE_BACKGROUND
        noticesTable.foreground = Styles.TEXT_COLOR
        scrollPanel.add(noticesTable.tableHeader, BorderLayout.NORTH)
        scrollPanel.add(noticesTable, BorderLayout.CENTER)
        add(scrollPanel)
        add(noticesToolBar, BorderLayout.SOUTH)
        */

        setSize(1000, 700)
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
    }

    fun loadData() {
        val dbRepo = app.getDatabaseRepo()
        if(dbRepo == null) {
            log.error("databaseRepo ist null")
            return
        }
        val worker = LoadNoticesWorker(dbRepo, noticesTableModel)
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