package de.heikozelt.wegefrei.gui

import de.heikozelt.wegefrei.DatabaseService
import de.heikozelt.wegefrei.entities.Notice
import de.heikozelt.wegefrei.model.NoticesTableModel
import org.slf4j.LoggerFactory
import java.util.*
import javax.swing.SwingWorker

/**
 * Läd die Meldungen im Übersichts-Fenster
 */
class LoadNoticesWorker(
    private val databaseService: DatabaseService,
    private val noticesTableModel: NoticesTableModel
)
: SwingWorker<MutableList<Notice>, MutableList<Notice>>() {

    private val log = LoggerFactory.getLogger(this::class.java.canonicalName)
    private var notices: MutableList<Notice>? = null

    /**
     * This is done in own Thread
     */
    override fun doInBackground(): MutableList<Notice>? {
        log.info("doInBackground()")
        notices = LinkedList(databaseService.getAllNoticesDesc())
        return notices
    }

    /**
     * this is done in the Swing Event Dispatcher Thread (EDT)
     */
    override fun done() {
        notices?.let {
            noticesTableModel.setNoticesList(it)
        }
    }

}